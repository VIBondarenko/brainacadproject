package com.brainacad.ecs.service;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;
import com.brainacad.ecs.security.Role;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service for initializing default users in the database
 * Creates system users with proper roles and encrypted passwords
 */
@Service
@Transactional
public class UserInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(UserInitializationService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Initialize default users if database is empty
     * This method runs after the application context is fully loaded
     */
    @PostConstruct
    public void initializeUsers() {
        logger.info("Checking if default users need to be created...");
        
        // Check if any users exist
        long userCount = userRepository.count();
        if (userCount == 0) {
            logger.info("No users found in database. Creating default users...");
            createDefaultUsers();
        } else {
            logger.info("Found {} existing users in database. Skipping initialization.", userCount);
        }
    }

    /**
     * Create all default system users
     */
    private void createDefaultUsers() {
        try {
            // Create each default user
            createUser("superadmin", "SuperAdmin123!", "Super", "Administrator", "superadmin@brainacad.com", Role.SUPER_ADMIN);
            createUser("admin", "Admin123!", "System", "Administrator", "admin@brainacad.com", Role.ADMIN);
            createUser("manager", "Manager123!", "Education", "Manager", "manager@brainacad.com", Role.MANAGER);
            createUser("teacher", "Teacher123!", "Default", "Teacher", "teacher@brainacad.com", Role.TEACHER);
            createUser("analyst", "Analyst123!", "Data", "Analyst", "analyst@brainacad.com", Role.ANALYST);
            createUser("moderator", "Moderator123!", "Content", "Moderator", "moderator@brainacad.com", Role.MODERATOR);
            createUser("student", "Student123!", "Test", "Student", "student@brainacad.com", Role.STUDENT);
            createUser("guest", "Guest123!", "Guest", "User", "guest@brainacad.com", Role.GUEST);

            logger.info("Successfully created all default users");
        } catch (Exception e) {
            logger.error("Error creating default users", e);
            throw new RuntimeException("Failed to initialize default users", e);
        }
    }

    /**
     * Create a single user with the given parameters
     */
    private void createUser(String username, String password, String firstName, String lastName, String email, Role role) {
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByUsernameIgnoreCase(username);
            if (existingUser.isPresent()) {
                logger.info("User '{}' already exists, skipping creation", username);
                return;
            }

            // Get default age based on role
            Integer age = getDefaultAge(role);
            
            // Create new user using constructor
            String encodedPassword = passwordEncoder.encode(password);
            User user = new User(firstName, lastName, age, username, encodedPassword, email, role);

            userRepository.save(user);
            logger.info("Created user: {} with role: {}", username, role);

        } catch (Exception e) {
            logger.error("Error creating user: {}", username, e);
            throw new RuntimeException("Failed to create user: " + username, e);
        }
    }

    /**
     * Get default age based on role
     */
    private Integer getDefaultAge(Role role) {
        return switch (role) {
            case SUPER_ADMIN, ADMIN -> 35;
            case MANAGER -> 32;
            case TEACHER, ANALYST -> 30;
            case MODERATOR -> 28;
            case STUDENT -> 22;
            case GUEST -> 25;
        };
    }

    /**
     * Method to reset admin password (for emergency access)
     * Can be called manually if needed
     */
    public void resetAdminPassword(String newPassword) {
        logger.warn("Resetting admin password...");
        
        Optional<User> adminUser = userRepository.findByUsernameIgnoreCase("admin");
        if (adminUser.isPresent()) {
            User admin = adminUser.get();
            admin.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(admin);
            logger.info("Admin password has been reset");
        } else {
            logger.error("Admin user not found for password reset");
            throw new RuntimeException("Admin user not found");
        }
    }

    /**
     * Get initialization status
     */
    public boolean isInitialized() {
        return userRepository.count() > 0;
    }

    /**
     * Get users count by role
     */
    public long getUserCountByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
