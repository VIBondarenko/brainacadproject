
package com.brainacad.ecs.service;

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

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;

@Service
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    
    private final Map<String, TokenInfo> tokens = new HashMap<>();
    private final long tokenExpirySeconds;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public PasswordResetService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            @Value("${security.password-reset.token-expiry-seconds:3600}") long tokenExpirySeconds
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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
        Optional<User> userOpt = userRepository.findByEmail(info.email);
        if (userOpt.isEmpty()) {
            tokens.remove(token);
            return false;
        }
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
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
