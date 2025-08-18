package com.brainacad.ecs.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brainacad.ecs.entity.UserSession;

/**
 * Repository for working with user sessions
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    /**
     * Find session by sessionId
     */
    Optional<UserSession> findBySessionId(String sessionId);

    /**
     * Find all active user sessions
     */
    List<UserSession> findByUserIdAndActiveTrue(Long userId);

    /**
     * Find all user sessions (active and inactive)
     */
    List<UserSession> findByUserIdOrderByLoginTimeDesc(Long userId);

    /**
     * Count active user sessions
     */
    long countByUserIdAndActiveTrue(Long userId);

    /**
     * Deactivate session by sessionId
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime WHERE s.sessionId = :sessionId")
    int deactivateSession(@Param("sessionId") String sessionId, @Param("logoutTime") LocalDateTime logoutTime);

    /**
     * Деактивировать все сессии пользователя кроме текущей
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime " +
           "WHERE s.userId = :userId AND s.sessionId != :currentSessionId AND s.active = true")
    int deactivateAllUserSessionsExcept(@Param("userId") Long userId, 
                                       @Param("currentSessionId") String currentSessionId, 
                                       @Param("logoutTime") LocalDateTime logoutTime);

    /**
     * Деактивировать все сессии пользователя
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime " +
           "WHERE s.userId = :userId AND s.active = true")
    int deactivateAllUserSessions(@Param("userId") Long userId, @Param("logoutTime") LocalDateTime logoutTime);

    /**
     * Update last activity time
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.lastActivity = :lastActivity WHERE s.sessionId = :sessionId")
    int updateLastActivity(@Param("sessionId") String sessionId, @Param("lastActivity") LocalDateTime lastActivity);

    /**
     * Найти неактивные сессии старше определенного времени для очистки
     */
    @Query("SELECT s FROM UserSession s WHERE s.active = false AND s.logoutTime < :cutoffTime")
    List<UserSession> findInactiveSessionsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Найти активные сессии без активности дольше определенного времени
     */
    @Query("SELECT s FROM UserSession s WHERE s.active = true AND s.lastActivity < :cutoffTime")
    List<UserSession> findActiveSessionsWithoutActivitySince(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Найти все активные сессии
     */
    List<UserSession> findByActiveTrue();

    /**
     * Найти неактивные сессии (для очистки)
     */
    @Query("SELECT s FROM UserSession s WHERE s.active = true AND s.lastActivity < :cutoffTime")
    List<UserSession> findInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Очистить неактивные сессии
     */
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime " +
           "WHERE s.active = true AND s.lastActivity < :cutoffTime")
    int cleanupInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime, @Param("logoutTime") LocalDateTime logoutTime);
}
