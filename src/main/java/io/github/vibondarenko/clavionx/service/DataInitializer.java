package io.github.vibondarenko.clavionx.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Role;

/**
 * Data initializer for creating default system administrator
 * Runs only if no users exist in the database
 */
@Component
@Order(1) // Execute before EmailMigrationService
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for DataInitializer.
     * @param userRepository the user repository
     * @param passwordEncoder the password encoder
     */
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Runs the data initialization logic on application startup.
     * Creates a default administrator if no users exist.
     * @param args command line arguments
     * @throws Exception if an error occurs during initialization
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initializeDefaultAdmin();
    }

    /**
     * Initializes the default administrator if no users exist.
     */
    private void initializeDefaultAdmin() {
        try {
            logger.info("Checking if initial data setup is needed...");
            
            // Check if any users exist in the database
            long userCount = userRepository.count();
            
            if (userCount == 0) {
                logger.info("No users found in database. Creating default administrator...");
                
                // Create default super admin user
                User defaultAdmin = new User(
                    "System", 
                    "Administrator", 
                    "superadmin", 
                    passwordEncoder.encode("superadmin"), 
                    "clavionx@gmail.com", 
                    Role.SUPER_ADMIN
                );
                
                // Set additional properties
                defaultAdmin.setEnabled(true);
                defaultAdmin.setAccountNonExpired(true);
                defaultAdmin.setAccountNonLocked(true);
                defaultAdmin.setCredentialsNonExpired(true);
                
                // Save to database
                userRepository.save(defaultAdmin);
                
                logger.info("✅ Default administrator created successfully!");
                logger.info("📧 Email: clavionx@gmail.com");
                logger.info("👤 Username: superadmin");
                logger.info("🔑 Password: superadmin");
                logger.info("⚠️  IMPORTANT: Please change the default password after first login!");
                logger.info("⚠️  IMPORTANT: Please change the default email after first login!");
            } else {
                logger.info("Users already exist in database. Skipping initial data setup.");
                logger.info("Current user count: {}", userCount);
            }
            
        } catch (Exception e) {
            throw new DataInitializationException("Failed to create default administrator", e);
        }
    }
    /**
     * Exception thrown when data initialization fails.
     */
    private static class DataInitializationException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        DataInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}