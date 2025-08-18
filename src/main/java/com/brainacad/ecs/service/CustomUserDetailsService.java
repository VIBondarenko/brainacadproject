package com.brainacad.ecs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;

/**
 * Custom UserDetailsService for Spring Security
 * Loads users from the database for authentication
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);
        
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        
        logger.info("User found: {} with role: {}", user.getUsername(), user.getRole());
        logger.info("User ID: {}", user.getId());
        logger.info("User class: {}", user.getClass().getName());
        logger.info("User name: {}", user.getName());
        logger.debug("User enabled: {}, account non-expired: {}, credentials non-expired: {}, account non-locked: {}", 
                    user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked());
        
        return user; // User implements UserDetails
    }
}
