package io.github.vibondarenko.clavionx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.vibondarenko.clavionx.service.SessionService;

/**
 * Scheduled tasks for session management
 */
@Component
public class SessionScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(SessionScheduledTasks.class);

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
            if (cleanedCount > 0) {
                logger.info("Scheduled cleanup: cleaned {} inactive sessions", cleanedCount);
            }
        } catch (Exception e) {
            logger.error("Error during scheduled inactive session cleanup: {}", e.getMessage());
        }
    }

    /**
     * Clean up old sessions daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldSessions() {
        try {
            sessionService.cleanupOldSessions();
            logger.info("Scheduled cleanup: old sessions cleanup completed");
        } catch (Exception e) {
            logger.error("Error during scheduled old session cleanup: {}", e.getMessage());
        }
    }

    /**
     * Deactivate inactive sessions every 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes = 1800000ms
    public void deactivateInactiveSessions() {
        try {
            sessionService.deactivateInactiveSessions();
            logger.debug("Scheduled task: inactive sessions deactivation completed");
        } catch (Exception e) {
            logger.error("Error during scheduled inactive session deactivation: {}", e.getMessage());
        }
    }
}



