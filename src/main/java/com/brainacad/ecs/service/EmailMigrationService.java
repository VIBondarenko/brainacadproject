package com.brainacad.ecs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

/**
 * Service to migrate existing users to have email addresses
 */
@Service
public class EmailMigrationService implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(EmailMigrationService.class.getName());

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
                logger.info("Found " + nullEmailCount + " users without email addresses. Updating...");
                
                // Update users without email to have generated email addresses based on username
                int updatedCount = jdbcTemplate.update(
                    "UPDATE users SET email = username || '@brainacad.com' WHERE email IS NULL");
                
                logger.info("Successfully updated " + updatedCount + " users with email addresses");
            } else {
                logger.info("All users already have email addresses. No migration needed.");
            }
            
        } catch (Exception e) {
            logger.severe("Error during email migration: " + e.getMessage());
            throw e;
        }
    }
}
