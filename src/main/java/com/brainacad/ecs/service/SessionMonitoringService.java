package com.brainacad.ecs.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brainacad.ecs.entity.UserSession;
import com.brainacad.ecs.repository.UserSessionRepository;

/**
 * Service for monitoring and analyzing session statistics
 */
@Service
@Transactional(readOnly = true)
public class SessionMonitoringService {

    private static final Logger logger = Logger.getLogger(SessionMonitoringService.class.getName());

    private final UserSessionRepository sessionRepository;

    public SessionMonitoringService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    /**
     * Get comprehensive session statistics
     */
    public Map<String, Object> getSessionStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Active sessions count
        long activeSessions = sessionRepository.countByActiveTrue();
        stats.put("activeSessions", activeSessions);

        // Total sessions today
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        long sessionsToday = sessionRepository.countByLoginTimeAfter(startOfDay);
        stats.put("sessionsToday", sessionsToday);

        // Average session duration (for completed sessions)
        Double avgDuration = sessionRepository.getAverageSessionDuration();
        stats.put("averageSessionDurationMinutes", avgDuration != null ? avgDuration : 0.0);

        // Peak concurrent sessions (last 24 hours)
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        long peakConcurrent = sessionRepository.getPeakConcurrentSessions(last24Hours);
        stats.put("peakConcurrentSessions24h", peakConcurrent);

        // Session distribution by device type
        Map<String, Long> deviceStats = getDeviceStatistics();
        stats.put("deviceDistribution", deviceStats);

        // Most active users (top 10)
        List<Map<String, Object>> topUsers = getTopActiveUsers(10);
        stats.put("topActiveUsers", topUsers);

        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Session statistics generated: %d active sessions, %d sessions today", 
                activeSessions, sessionsToday));
        }

        return stats;
    }

    /**
     * Get session statistics by device type
     */
    public Map<String, Long> getDeviceStatistics() {
        List<UserSession> activeSessions = sessionRepository.findByActiveTrue();
        
        Map<String, Long> deviceCounts = new HashMap<>();
        deviceCounts.put("Desktop", 0L);
        deviceCounts.put("Mobile", 0L);
        deviceCounts.put("iOS Device", 0L);
        deviceCounts.put("Windows PC", 0L);
        deviceCounts.put("Mac", 0L);
        deviceCounts.put("Linux", 0L);
        deviceCounts.put("Unknown", 0L);

        for (UserSession session : activeSessions) {
            String deviceInfo = session.getDeviceInfo();
            if (deviceInfo != null) {
                deviceCounts.merge(deviceInfo, 1L, Long::sum);
            } else {
                deviceCounts.merge("Unknown", 1L, Long::sum);
            }
        }

        return deviceCounts;
    }

    /**
     * Get top active users by session count
     */
    public List<Map<String, Object>> getTopActiveUsers(int limit) {
        return sessionRepository.getTopActiveUsers(limit);
    }

    /**
     * Get session activity for the last N hours
     */
    public List<Map<String, Object>> getSessionActivityByHour(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return sessionRepository.getSessionActivityByHour(startTime);
    }

    /**
     * Check for suspicious session activity
     */
    public List<Map<String, Object>> getSuspiciousActivity() {
        // Find users with too many active sessions
        int maxAllowed = 10; // Configurable threshold
        List<Map<String, Object>> suspicious = sessionRepository.getUsersWithExcessiveSessions(maxAllowed);
        
        // Find sessions from unusual locations (multiple IPs for same user)
        List<Map<String, Object>> multipleIPs = sessionRepository.getUsersWithMultipleIPs();
        
        // Combine results
        suspicious.addAll(multipleIPs);
        
        if (!suspicious.isEmpty() && logger.isLoggable(java.util.logging.Level.WARNING)) {
            logger.warning(String.format("Found %d suspicious session activities", suspicious.size()));
        }
        
        return suspicious;
    }

    /**
     * Get detailed session information for admin dashboard
     */
    public Map<String, Object> getDetailedSessionInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // Basic statistics
        info.putAll(getSessionStatistics());
        
        // Session activity trends
        info.put("hourlyActivity", getSessionActivityByHour(24));
        
        // Security alerts
        info.put("suspiciousActivity", getSuspiciousActivity());
        
        // Recent sessions
        List<UserSession> recentSessions = sessionRepository.findTop20ByOrderByLoginTimeDesc();
        info.put("recentSessions", recentSessions);
        
        return info;
    }
}
