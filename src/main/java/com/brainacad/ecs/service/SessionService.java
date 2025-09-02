package com.brainacad.ecs.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.entity.UserSession;
import com.brainacad.ecs.repository.UserSessionRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for managing user sessions
 */
@Service
@Transactional
public class SessionService {

    private static final Logger logger = Logger.getLogger(SessionService.class.getName());
    
    @Value("${ecs.session.max-sessions-per-user:5}")
    private int maxSessionsPerUser;
    
    @Value("${ecs.session.inactive-timeout-hours:24}")
    private int inactiveTimeoutHours;

    private final UserSessionRepository sessionRepository;

    public SessionService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Create new session for user login
     */
    public UserSession createSession(User user, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Creating new session for user: %s from IP: %s", user.getUsername(), ipAddress));
        }

        // Check session limit
        long activeSessionsCount = sessionRepository.countByUserIdAndActiveTrue(user.getId());
        if (activeSessionsCount >= maxSessionsPerUser) {
            // Deactivate the oldest session
            deactivateOldestSession(user.getId());
        }

        UserSession session = new UserSession(sessionId, user.getId(), ipAddress, userAgent);
        return sessionRepository.save(session);
    }

    /**
     * Update session last activity time
     */
    public void updateLastActivity(String sessionId) {
        sessionRepository.updateLastActivity(sessionId, LocalDateTime.now());
    }

    /**
     * Terminate session on user logout
     */
    @Transactional
    public void terminateSession(String sessionId) {
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Attempting to terminate session: %s", sessionId));
        }
        try {
            sessionRepository.deactivateSession(sessionId, LocalDateTime.now());
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info(String.format("Session terminated successfully: %s", sessionId));
            }
        } catch (Exception e) {
            logger.severe(String.format("Failed to terminate session: %s, error: %s", sessionId, e.getMessage()));
            throw new RuntimeException("Failed to terminate session with ID: " + sessionId + ". Exception: " + e.getClass().getSimpleName(), e);
        }
    }

    /**
     * Terminate all user sessions except the current one
     */
    public int terminateAllUserSessionsExcept(Long userId, String currentSessionId) {
        int deactivatedCount = sessionRepository.deactivateAllUserSessionsExcept(
                userId, currentSessionId, LocalDateTime.now());
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Terminated %d sessions for user ID: %d", deactivatedCount, userId));
        }
        return deactivatedCount;
    }

    /**
     * Terminate all sessions for a user
     */
    public int terminateAllUserSessions(Long userId) {
        int deactivatedCount = sessionRepository.deactivateAllUserSessions(userId, LocalDateTime.now());
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Terminated all sessions for user ID: %d", userId));
        }
        return deactivatedCount;
    }

    /**
     * Get all active user sessions
     */
    @Transactional(readOnly = true)
    public List<UserSession> getActiveUserSessions(Long userId) {
        return sessionRepository.findByUserIdAndActiveTrue(userId);
    }

    /**
     * Get all user sessions (active and inactive)
     */
    @Transactional(readOnly = true)
    public List<UserSession> getAllUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByLoginTimeDesc(userId);
    }

    /**
     * Get session by sessionId
     */
    @Transactional(readOnly = true)
    public Optional<UserSession> getSessionById(String sessionId) {
        return sessionRepository.findBySessionId(sessionId);
    }

    /**
     * Check if the session is active
     */
    @Transactional(readOnly = true)
    public boolean isSessionActive(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
                .map(UserSession::isActive)
                .orElse(false);
    }

    /**
     * Deactivate the oldest active session of the user
     */
    private void deactivateOldestSession(Long userId) {
        List<UserSession> activeSessions = sessionRepository.findByUserIdAndActiveTrue(userId);
        if (!activeSessions.isEmpty()) {
            // Find the oldest session (with the minimum loginTime)
            UserSession oldestSession = activeSessions.stream()
                    .min((s1, s2) -> s1.getLoginTime().compareTo(s2.getLoginTime()))
                    .orElse(null);
            
            if (oldestSession != null) {
                sessionRepository.deactivateSession(oldestSession.getSessionId(), LocalDateTime.now());
                if (logger.isLoggable(java.util.logging.Level.INFO)) {
                    logger.info(String.format("Deactivated oldest session: %s", oldestSession.getSessionId()));
                }
            }
        }
    }

    /**
     * Get client IP address considering proxy servers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Clean up old inactive sessions (can be called on schedule)
     */
    public void cleanupOldSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        List<UserSession> oldSessions = sessionRepository.findInactiveSessionsOlderThan(cutoffTime);
        
        if (!oldSessions.isEmpty()) {
            sessionRepository.deleteAll(oldSessions);
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info(String.format("Cleaned up %d old sessions", oldSessions.size()));
            }
        }
    }

    /**
     * Deactivate sessions without activity for longer than a specified time
     */
    public void deactivateInactiveSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(inactiveTimeoutHours); // Configurable timeout
        List<UserSession> inactiveSessions = sessionRepository.findActiveSessionsWithoutActivitySince(cutoffTime);
        
        for (UserSession session : inactiveSessions) {
            sessionRepository.deactivateSession(session.getSessionId(), LocalDateTime.now());
        }
        
        if (!inactiveSessions.isEmpty() && logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Deactivated %d inactive sessions", inactiveSessions.size()));
        }
    }

    /**
     * Get all active sessions across all users.
     *
     * @return list of active sessions
     */
    public List<UserSession> getActiveSessions() {
        logger.info("Fetching all active sessions");
        return sessionRepository.findByActiveTrue();
    }

    /**
     * Get active sessions for a specific user.
     *
     * @param userId user ID
     * @return list of user's active sessions
     */
    public List<UserSession> getUserActiveSessions(Long userId) {
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Fetching active sessions for user: %d", userId));
        }
        return sessionRepository.findByUserIdAndActiveTrue(userId);
    }

    /**
     * Get session by session ID.
     *
     * @param sessionId session ID
     * @return UserSession or null if not found
     */
    public UserSession getSessionBySessionId(String sessionId) {
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Fetching session: %s", sessionId));
        }
        Optional<UserSession> optionalSession = sessionRepository.findBySessionId(sessionId);
        return optionalSession.orElse(null);
    }

    /**
     * Clean up inactive sessions and return count of cleaned sessions.
     *
     * @return number of sessions cleaned up
     */
    public int cleanupInactiveSessions() {
        logger.info("Starting cleanup of inactive sessions");
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(inactiveTimeoutHours); // Configurable timeout
        List<UserSession> inactiveSessions = sessionRepository.findInactiveSessions(cutoffTime);
        
        int cleanedCount = inactiveSessions.size();
        
        if (cleanedCount > 0) {
            sessionRepository.cleanupInactiveSessions(cutoffTime, LocalDateTime.now());
            if (logger.isLoggable(java.util.logging.Level.INFO)) {
                logger.info(String.format("Cleaned up %d inactive sessions", cleanedCount));
            }
        } else {
            logger.info("No inactive sessions found for cleanup");
        }
        
        return cleanedCount;
    }
    
    /**
     * Get the configured maximum sessions per user.
     *
     * @return maximum sessions per user
     */
    public int getMaxSessionsPerUser() {
        return maxSessionsPerUser;
    }
    
    /**
     * Get the configured inactive timeout in hours.
     *
     * @return inactive timeout hours
     */
    public int getInactiveTimeoutHours() {
        return inactiveTimeoutHours;
    }
}
