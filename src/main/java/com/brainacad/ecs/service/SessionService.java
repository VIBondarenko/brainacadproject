package com.brainacad.ecs.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
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
    private static final int MAX_SESSIONS_PER_USER = 5;

    @Autowired
    private UserSessionRepository sessionRepository;

    /**
     * Create new session for user login
     */
    public UserSession createSession(User user, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        logger.info("Creating new session for user: " + user.getUsername() + " from IP: " + ipAddress);

        // Проверить лимит сессий
        long activeSessionsCount = sessionRepository.countByUserIdAndActiveTrue(user.getId());
        if (activeSessionsCount >= MAX_SESSIONS_PER_USER) {
            // Деактивировать самую старую сессию
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
        logger.info("Attempting to terminate session: " + sessionId);
        try {
            sessionRepository.deactivateSession(sessionId, LocalDateTime.now());
            logger.info("Session terminated successfully: " + sessionId);
        } catch (Exception e) {
            logger.severe("Failed to terminate session: " + sessionId + ", error: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Завершить все сессии пользователя кроме текущей
     */
    public int terminateAllUserSessionsExcept(Long userId, String currentSessionId) {
        int deactivatedCount = sessionRepository.deactivateAllUserSessionsExcept(
                userId, currentSessionId, LocalDateTime.now());
        logger.info("Terminated " + deactivatedCount + " sessions for user ID: " + userId);
        return deactivatedCount;
    }

    /**
     * Завершить все сессии пользователя
     */
    public int terminateAllUserSessions(Long userId) {
        int deactivatedCount = sessionRepository.deactivateAllUserSessions(userId, LocalDateTime.now());
        logger.info("Terminated all sessions for user ID: " + userId);
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
     * Проверить, активна ли сессия
     */
    @Transactional(readOnly = true)
    public boolean isSessionActive(String sessionId) {
        return sessionRepository.findBySessionId(sessionId)
                .map(UserSession::isActive)
                .orElse(false);
    }

    /**
     * Деактивировать самую старую активную сессию пользователя
     */
    private void deactivateOldestSession(Long userId) {
        List<UserSession> activeSessions = sessionRepository.findByUserIdAndActiveTrue(userId);
        if (!activeSessions.isEmpty()) {
            // Найти самую старую сессию (с минимальным loginTime)
            UserSession oldestSession = activeSessions.stream()
                    .min((s1, s2) -> s1.getLoginTime().compareTo(s2.getLoginTime()))
                    .orElse(null);
            
            if (oldestSession != null) {
                sessionRepository.deactivateSession(oldestSession.getSessionId(), LocalDateTime.now());
                logger.info("Deactivated oldest session: " + oldestSession.getSessionId());
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
     * Очистить старые неактивные сессии (можно вызывать по расписанию)
     */
    public void cleanupOldSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(30);
        List<UserSession> oldSessions = sessionRepository.findInactiveSessionsOlderThan(cutoffTime);
        
        if (!oldSessions.isEmpty()) {
            sessionRepository.deleteAll(oldSessions);
            logger.info("Cleaned up " + oldSessions.size() + " old sessions");
        }
    }

    /**
     * Деактивировать сессии без активности дольше определенного времени
     */
    public void deactivateInactiveSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 24 часа без активности
        List<UserSession> inactiveSessions = sessionRepository.findActiveSessionsWithoutActivitySince(cutoffTime);
        
        for (UserSession session : inactiveSessions) {
            sessionRepository.deactivateSession(session.getSessionId(), LocalDateTime.now());
        }
        
        if (!inactiveSessions.isEmpty()) {
            logger.info("Deactivated " + inactiveSessions.size() + " inactive sessions");
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
        logger.info("Fetching active sessions for user: " + userId);
        return sessionRepository.findByUserIdAndActiveTrue(userId);
    }

    /**
     * Get session by session ID.
     *
     * @param sessionId session ID
     * @return UserSession or null if not found
     */
    public UserSession getSessionBySessionId(String sessionId) {
        logger.info("Fetching session: " + sessionId);
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
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24); // 24 hours timeout
        List<UserSession> inactiveSessions = sessionRepository.findInactiveSessions(cutoffTime);
        
        int cleanedCount = inactiveSessions.size();
        
        if (cleanedCount > 0) {
            sessionRepository.cleanupInactiveSessions(cutoffTime, LocalDateTime.now());
            logger.info("Cleaned up " + cleanedCount + " inactive sessions");
        } else {
            logger.info("No inactive sessions found for cleanup");
        }
        
        return cleanedCount;
    }
}
