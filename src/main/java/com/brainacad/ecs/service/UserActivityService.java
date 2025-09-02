package com.brainacad.ecs.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.brainacad.ecs.entity.UserActivity;
import com.brainacad.ecs.repository.UserActivityRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service for managing user activities and audit trail
 * Provides comprehensive activity tracking and analytics capabilities
 */
@Service
@Transactional
public class UserActivityService {

    private static final Logger logger = LoggerFactory.getLogger(UserActivityService.class);

    private final UserActivityRepository activityRepository;

    public UserActivityService(UserActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * Log user activity asynchronously
     */
    @Async
    public void logActivity(String actionType, String actionDescription) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                
                // Get current request for IP and User-Agent
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
                
                UserActivity activity = UserActivity.builder()
                        .username(auth.getName())
                        .actionType(actionType)
                        .actionDescription(actionDescription)
                        .ipAddress(request != null ? getClientIpAddress(request) : "unknown")
                        .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
                        .status("SUCCESS")
                        .build();
                
                // Try to get user ID from authentication principal
                if (auth.getPrincipal() instanceof com.brainacad.ecs.entity.User user) {
                    activity.setUserId(user.getId());
                }
                
                activityRepository.save(activity);
                logger.debug("Activity logged: {} - {}", actionType, actionDescription);
            }
        } catch (Exception e) {
            logger.error("Failed to log activity: {} - {}", actionType, actionDescription, e);
        }
    }

    /**
     * Log activity with resource information
     */
    @Async
    public void logActivity(String actionType, String actionDescription, String resourceType, Long resourceId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
                
                UserActivity activity = UserActivity.builder()
                        .username(auth.getName())
                        .actionType(actionType)
                        .actionDescription(actionDescription)
                        .resourceType(resourceType)
                        .resourceId(resourceId)
                        .ipAddress(request != null ? getClientIpAddress(request) : "unknown")
                        .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
                        .status("SUCCESS")
                        .build();
                
                if (auth.getPrincipal() instanceof com.brainacad.ecs.entity.User user) {
                    activity.setUserId(user.getId());
                }
                
                activityRepository.save(activity);
                logger.debug("Activity logged: {} - {} on {} {}", actionType, actionDescription, resourceType, resourceId);
            }
        } catch (Exception e) {
            logger.error("Failed to log activity: {} - {}", actionType, actionDescription, e);
        }
    }

    /**
     * Log failed activity
     */
    @Async
    public void logFailedActivity(String actionType, String actionDescription, String errorDetails) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "anonymous";
            
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attrs != null ? attrs.getRequest() : null;
            
            UserActivity activity = UserActivity.builder()
                    .username(username)
                    .actionType(actionType)
                    .actionDescription(actionDescription)
                    .ipAddress(request != null ? getClientIpAddress(request) : "unknown")
                    .userAgent(request != null ? request.getHeader("User-Agent") : "unknown")
                    .status("FAILURE")
                    .details(errorDetails)
                    .build();
            
            if (auth != null && auth.getPrincipal() instanceof com.brainacad.ecs.entity.User user) {
                activity.setUserId(user.getId());
            }
            
            activityRepository.save(activity);
            logger.warn("Failed activity logged: {} - {}", actionType, actionDescription);
        } catch (Exception e) {
            logger.error("Failed to log failed activity: {} - {}", actionType, actionDescription, e);
        }
    }

    /**
     * Get user activities with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserActivity> getUserActivities(Long userId, Pageable pageable) {
        return activityRepository.findByUserIdOrderByTimestampDesc(userId, pageable);
    }

    /**
     * Get recent user activities
     */
    @Transactional(readOnly = true)
    public List<UserActivity> getRecentUserActivities(Long userId, int limit) {
        return activityRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .limit(limit)
                .toList();
    }

    /**
     * Get recent activities for all users
     */
    @Transactional(readOnly = true)
    public List<UserActivity> getRecentActivities(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityRepository.findRecentActivities(since);
    }

    /**
     * Get activities by action type with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserActivity> getActivitiesByActionType(String actionType, Pageable pageable) {
        return activityRepository.findByActionTypeOrderByTimestampDesc(actionType, pageable);
    }

    /**
     * Get activity statistics by action type
     */
    @Transactional(readOnly = true)
    public List<Object[]> getActivityStatistics() {
        return activityRepository.getActivityStatsByActionType();
    }

    /**
     * Get top active users
     */
    @Transactional(readOnly = true)
    public List<Object[]> getTopActiveUsers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return activityRepository.findTopActiveUsers(since);
    }

    /**
     * Clean up old activities
     */
    @Transactional
    public void cleanupOldActivities(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        try {
            activityRepository.deleteByTimestampBefore(cutoffDate);
            logger.info("Cleaned up activities older than {} days", daysToKeep);
        } catch (Exception e) {
            logger.error("Failed to cleanup old activities", e);
        }
    }

    /**
     * Get suspicious activities from IP
     */
    @Transactional(readOnly = true)
    public List<UserActivity> getSuspiciousActivities(String ipAddress, int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return activityRepository.findSuspiciousActivitiesByIP(ipAddress, since);
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

    // Pre-defined activity types for consistency
    public static final class ActivityType {
        private ActivityType() {
            // Prevent instantiation
        }
        public static final String LOGIN = "LOGIN";
        public static final String LOGOUT = "LOGOUT";
        public static final String COURSE_VIEW = "COURSE_VIEW";
        public static final String COURSE_CREATE = "COURSE_CREATE";
        public static final String COURSE_UPDATE = "COURSE_UPDATE";
        public static final String COURSE_DELETE = "COURSE_DELETE";
        public static final String USER_VIEW = "USER_VIEW";
        public static final String USER_UPDATE = "USER_UPDATE";
        public static final String PROFILE_VIEW = "PROFILE_VIEW";
        public static final String PROFILE_UPDATE = "PROFILE_UPDATE";
        public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
        public static final String SESSION_TERMINATE = "SESSION_TERMINATE";
        public static final String ADMIN_ACCESS = "ADMIN_ACCESS";
        public static final String API_ACCESS = "API_ACCESS";
        public static final String SECURITY_VIOLATION = "SECURITY_VIOLATION";
    }
}
