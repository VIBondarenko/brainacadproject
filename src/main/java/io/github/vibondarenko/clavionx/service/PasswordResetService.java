
package io.github.vibondarenko.clavionx.service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;

@Service
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    
    private final Map<String, TokenInfo> tokens = new HashMap<>();
    private final long tokenExpirySeconds;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordHistoryService passwordHistoryService;
    private final PasswordPolicyService passwordPolicyService;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            PasswordPolicyService passwordPolicyService,
            PasswordHistoryService passwordHistoryService,
            @Value("${security.password-reset.token-expiry-seconds:3600}") long tokenExpirySeconds
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordHistoryService = passwordHistoryService;
        this.tokenExpirySeconds = tokenExpirySeconds;
    }

    @Transactional
    public void sendResetLink(String email, String baseUrl) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Do not reveal that the email was not found
            logger.debug("User not found for email: {}", email);
            return;
        }
        String token = UUID.randomUUID().toString();
        tokens.put(token, new TokenInfo(email, Instant.now().plusSeconds(tokenExpirySeconds)));
        String resetLink = baseUrl + "/reset-password?token=" + token;
        logger.debug("Generated password reset token for user: {}", email);
        logger.debug("Reset link generated: {}", resetLink);
        emailService.sendPasswordResetEmail(userOpt.get(), resetLink);
    } 

    public boolean isValidToken(String token) {
        TokenInfo info = tokens.get(token);
        return info != null && Instant.now().isBefore(info.expiry);
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        TokenInfo info = tokens.get(token);
        if (info == null || Instant.now().isAfter(info.expiry)) {
            return false;
        }
        // Enforce policy before saving
        String error = passwordPolicyService.validate(newPassword);
        if (error != null) {
            return false;
        }
        Optional<User> userOpt = userRepository.findByEmail(info.email);
        if (userOpt.isEmpty()) {
            tokens.remove(token);
            return false;
        }
        User user = userOpt.get();
        // Check history and current password
        passwordHistoryService.validateNotSameAsCurrent(user, newPassword);
        passwordHistoryService.validateNotInHistory(user, newPassword);
    user.setPassword(passwordEncoder.encode(newPassword));
    passwordHistoryService.storePasswordHash(user, user.getPassword());
        userRepository.save(user);
        tokens.remove(token);
        return true;
    }

    private static class TokenInfo {
        String email;
        Instant expiry;
        TokenInfo(String email, Instant expiry) {
            this.email = email;
            this.expiry = expiry;
        }
    }
}



