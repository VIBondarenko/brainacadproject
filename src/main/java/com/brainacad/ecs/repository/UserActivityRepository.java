package com.brainacad.ecs.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brainacad.ecs.entity.UserActivity;

/**
 * Repository interface for UserActivity entities
 * Provides data access methods for user activity tracking and analytics
 */
@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    /**
     * Find activities by user ID ordered by timestamp descending
     */
    List<UserActivity> findByUserIdOrderByTimestampDesc(Long userId);

    /**
     * Find activities by user ID with pagination
     */
    Page<UserActivity> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);

    /**
     * Find activities by username ordered by timestamp descending
     */
    List<UserActivity> findByUsernameOrderByTimestampDesc(String username);

    /**
     * Find activities by action type
     */
    List<UserActivity> findByActionTypeOrderByTimestampDesc(String actionType);

    /**
     * Find activities by action type with pagination
     */
    Page<UserActivity> findByActionTypeOrderByTimestampDesc(String actionType, Pageable pageable);

    /**
     * Find activities within date range
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.timestamp BETWEEN :startDate AND :endDate ORDER BY ua.timestamp DESC")
    List<UserActivity> findByTimestampBetween(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);

    /**
     * Find activities by resource type and ID
     */
    List<UserActivity> findByResourceTypeAndResourceIdOrderByTimestampDesc(String resourceType, Long resourceId);

    /**
     * Find recent activities (last N hours)
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.timestamp >= :since ORDER BY ua.timestamp DESC")
    List<UserActivity> findRecentActivities(@Param("since") LocalDateTime since);

    /**
     * Count activities by user and date range
     */
    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId AND ua.timestamp BETWEEN :startDate AND :endDate")
    long countUserActivitiesByDateRange(@Param("userId") Long userId, 
                                      @Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Find activities by status
     */
    List<UserActivity> findByStatusOrderByTimestampDesc(String status);

    /**
     * Find failed activities (for monitoring)
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.status IN ('FAILURE', 'ERROR') ORDER BY ua.timestamp DESC")
    List<UserActivity> findFailedActivities();

    /**
     * Get activity statistics by action type
     */
    @Query("SELECT ua.actionType, COUNT(ua) FROM UserActivity ua GROUP BY ua.actionType ORDER BY COUNT(ua) DESC")
    List<Object[]> getActivityStatsByActionType();

    /**
     * Get daily activity count for a user
     */
    @Query("SELECT DATE(ua.timestamp), COUNT(ua) FROM UserActivity ua WHERE ua.userId = :userId " +
           "AND ua.timestamp >= :since GROUP BY DATE(ua.timestamp) ORDER BY DATE(ua.timestamp)")
    List<Object[]> getDailyActivityCount(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    /**
     * Find top active users
     */
    @Query("SELECT ua.userId, ua.username, COUNT(ua) as activityCount FROM UserActivity ua " +
           "WHERE ua.timestamp >= :since GROUP BY ua.userId, ua.username " +
           "ORDER BY COUNT(ua) DESC")
    List<Object[]> findTopActiveUsers(@Param("since") LocalDateTime since);

    /**
     * Clean up old activities (older than specified date)
     */
    void deleteByTimestampBefore(LocalDateTime cutoffDate);

    /**
     * Find suspicious activities (multiple failed attempts from same IP)
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.ipAddress = :ipAddress " +
           "AND ua.status = 'FAILURE' AND ua.timestamp >= :since " +
           "ORDER BY ua.timestamp DESC")
    List<UserActivity> findSuspiciousActivitiesByIP(@Param("ipAddress") String ipAddress, 
                                                   @Param("since") LocalDateTime since);
}
