package com.brainacad.ecs.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.util.logging.Logger;

/**
 * Service to migrate existing users to have email addresses
 */
@Service
public class EmailMigrationService implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(EmailMigrationService.class.getName());

    private final JdbcTemplate jdbcTemplate;

    public EmailMigrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        migrateUserEmails();
    }

    private void migrateUserEmails() {
        try {
            logger.info("Starting email migration for existing users...");
            
            // Check if any users have null email
            Integer nullEmailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email IS NULL", Integer.class);
            
            if (nullEmailCount != null && nullEmailCount > 0) {
                if (logger.isLoggable(java.util.logging.Level.INFO)) {
                    logger.info(String.format("Found %d users without email addresses. Updating...", nullEmailCount));
                }
                
                // Update users without email to have generated email addresses based on username
                int updatedCount = jdbcTemplate.update(
                    "UPDATE users SET email = username || '@brainacad.com' WHERE email IS NULL");
                
                if (logger.isLoggable(java.util.logging.Level.INFO)) {
                    logger.info(String.format("Successfully updated %d users with email addresses", updatedCount));
                }
            } else {
                logger.info("All users already have email addresses. No migration needed.");
            }
            
        } catch (DataAccessException dae) {
            logger.severe(String.format("Data access error during email migration: %s", dae.getMessage()));
            // Add contextual information and rethrow
            throw new RuntimeException("EmailMigrationService: Data access error while migrating user emails", dae);
        } catch (RuntimeException re) {
            logger.severe(String.format("Unexpected runtime error during email migration: %s", re.getMessage()));
            // Add contextual information and rethrow
            throw new RuntimeException("EmailMigrationService: Unexpected error while migrating user emails", re);
        }
    }
}
