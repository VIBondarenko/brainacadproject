package io.github.vibondarenko.clavionx.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.dto.UserCreateDto;
import io.github.vibondarenko.clavionx.entity.ActivationToken;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.ActivationTokenRepository;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Role;

@Service

public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordPolicyService passwordPolicyService;
    private final PasswordHistoryService passwordHistoryService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, ActivationTokenRepository activationTokenRepository, PasswordPolicyService passwordPolicyService, PasswordHistoryService passwordHistoryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.activationTokenRepository = activationTokenRepository;
        this.passwordPolicyService = passwordPolicyService;
        this.passwordHistoryService = passwordHistoryService;
    }

    @Transactional
    public User createUser(UserCreateDto dto, String baseUrl) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    passwordPolicyService.validateOrThrow(dto.getPassword());
    User user = new User(
                dto.getName(),
                dto.getLastName(),
                dto.getUsername(),
        passwordEncoder.encode(dto.getPassword()),
                dto.getEmail(),
                Role.valueOf(dto.getRole())
        );
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setEnabled(dto.isEnabled());
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
    userRepository.save(user);
    // store initial password hash
    passwordHistoryService.storePasswordHash(user, user.getPassword());

        // Generate and send activation token if user is not enabled
        if (!user.isEnabled()) {
            String token = java.util.UUID.randomUUID().toString();
            ActivationToken activationToken = new ActivationToken(token, user, java.time.LocalDateTime.now().plusDays(1));
            activationTokenRepository.save(activationToken);
            String activationLink = baseUrl + "/activate?token=" + token;
            emailService.sendActivationEmail(user, activationLink);
        }
        return user;
    }

    @Transactional
    public User updateUserFromDto(Long id, UserCreateDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        existingUser.setName(dto.getName());
        existingUser.setLastName(dto.getLastName());
        existingUser.setPhoneNumber(dto.getPhoneNumber());
        existingUser.setUsername(dto.getUsername());
        existingUser.setEmail(dto.getEmail());
        existingUser.setRole(Role.valueOf(dto.getRole()));
        existingUser.setEnabled(dto.isEnabled());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            passwordPolicyService.validateOrThrow(dto.getPassword());
            passwordHistoryService.validateNotSameAsCurrent(existingUser, dto.getPassword());
            passwordHistoryService.validateNotInHistory(existingUser, dto.getPassword());
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            passwordHistoryService.storePasswordHash(existingUser, existingUser.getPassword());
        }
        userRepository.save(existingUser);
        return existingUser;
    }
}



