package io.github.vibondarenko.clavionx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;

/**
 * Custom UserDetailsService for Spring Security
 * Loads users from the database for authentication
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;
    private final PersistentLoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository, PersistentLoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username or email: {}", usernameOrEmail);
        
        if (usernameOrEmail != null && loginAttemptService.isLocked(usernameOrEmail.toLowerCase())) {
            logger.warn("User is locked due to failed attempts: {}", usernameOrEmail);
            throw new LockedException("Account locked: " + usernameOrEmail);
        }

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found: " + usernameOrEmail);
                });
        
        logger.info("User found: {} with email: {} and role: {}", user.getUsername(), user.getEmail(), user.getRole());
        logger.info("User ID: {}", user.getId());
        logger.info("User class: {}", user.getClass().getName());
        logger.info("User name: {}", user.getName());
        logger.debug("User enabled: {}, account non-expired: {}, credentials non-expired: {}, account non-locked: {}", 
                    user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked());
        
        return user; // User implements UserDetails
    }
}



