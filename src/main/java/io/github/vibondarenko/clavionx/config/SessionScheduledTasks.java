package io.github.vibondarenko.clavionx.config;

import java.util.logging.Logger;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.vibondarenko.clavionx.service.SessionService;

/**
 * Scheduled tasks for session management
 */
@Component
public class SessionScheduledTasks {

    private static final Logger logger = Logger.getLogger(SessionScheduledTasks.class.getName());

    private final SessionService sessionService;

    public SessionScheduledTasks(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Clean up inactive sessions every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000ms
    public void cleanupInactiveSessions() {
        try {
            int cleanedCount = sessionService.cleanupInactiveSessions();
            if (cleanedCount > 0 && logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info(String.format("Scheduled cleanup: cleaned %d inactive sessions", cleanedCount));
            }
        } catch (Exception e) {
            logger.severe(String.format("Error during scheduled inactive session cleanup: %s", e.getMessage()));
        }
    }

    /**
     * Clean up old sessions daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldSessions() {
        try {
            sessionService.cleanupOldSessions();
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info("Scheduled cleanup: old sessions cleanup completed");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error during scheduled old session cleanup: %s", e.getMessage()));
        }
    }

    /**
     * Deactivate inactive sessions every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes = 1800000ms
    public void deactivateInactiveSessions() {
        try {
            sessionService.deactivateInactiveSessions();
            if (logger.isLoggable(java.util.logging.Level.FINE)) {
                logger.fine("Scheduled task: inactive sessions deactivation completed");
            }
        } catch (Exception e) {
            logger.severe(String.format("Error during scheduled inactive session deactivation: %s", e.getMessage()));
        }
    }
}



