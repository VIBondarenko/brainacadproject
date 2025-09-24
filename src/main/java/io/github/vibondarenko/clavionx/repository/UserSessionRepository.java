package io.github.vibondarenko.clavionx.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.UserSession;

/**
 * Repository for working with user sessions
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

	/**
	* Find session by sessionId
	* @param sessionId the session ID
	* @return Optional containing the UserSession if found, or empty if not found
	*/
	Optional<UserSession> findBySessionId(String sessionId);

	/**
	* Find all active user sessions
	* @param userId the user ID
	* @return list of active UserSession objects
	*/
	List<UserSession> findByUserIdAndActiveTrue(Long userId);

	/**
	* Find all user sessions (active and inactive)
	* @param userId the user ID
	* @return list of all UserSession objects for the user, sorted by login time descending
	*/
	List<UserSession> findByUserIdOrderByLoginTimeDesc(Long userId);

	/**
	 * Count active user sessions
	 * @param userId the user ID
	 * @return number of active sessions for the user
	 */
	long countByUserIdAndActiveTrue(Long userId);

	/**
	 * Deactivate session by sessionId
	 * @param sessionId the session ID
	 * @param logoutTime the time of logout
	 * @return number of rows affected (should be 1 if session was found and deactivated)
	 */
	@Modifying
	@Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime WHERE s.sessionId = :sessionId")
	int deactivateSession(@Param("sessionId") String sessionId, @Param("logoutTime") LocalDateTime logoutTime);

	/**
	 * Deactivate all user sessions except the current one
	 * @param userId the user ID
	 * @param currentSessionId the current session ID to exclude from deactivation
	 * @param logoutTime the time of logout
	 * @return number of rows affected (number of sessions deactivated)
	 */
	@Modifying
	@Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime " +
			"WHERE s.userId = :userId AND s.sessionId != :currentSessionId AND s.active = true")
	int deactivateAllUserSessionsExcept(@Param("userId") Long userId, 
										@Param("currentSessionId") String currentSessionId, 
										@Param("logoutTime") LocalDateTime logoutTime);

	/**
	 * Deactivate all user sessions
	 * @param userId the user ID
	 * @param logoutTime the time of logout
	 * @return number of rows affected (number of sessions deactivated)
	 */
	@Modifying
	@Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime " +
			"WHERE s.userId = :userId AND s.active = true")
	int deactivateAllUserSessions(@Param("userId") Long userId, @Param("logoutTime") LocalDateTime logoutTime);

	/**
	 * Update last activity time
	 * @param sessionId the session ID
	 * @param lastActivity the new last activity time
	 * @return number of rows affected (should be 1 if session was found and updated)
	 */
	@Modifying
	@Query("UPDATE UserSession s SET s.lastActivity = :lastActivity WHERE s.sessionId = :sessionId")
	int updateLastActivity(@Param("sessionId") String sessionId, @Param("lastActivity") LocalDateTime lastActivity);

	/**
	 * Find inactive sessions older than specified time for cleanup
	 * @param cutoffTime the cutoff time
	 * @return list of inactive UserSession objects older than cutoffTime
	 */
	@Query("SELECT s FROM UserSession s WHERE s.active = false AND s.logoutTime < :cutoffTime")
	List<UserSession> findInactiveSessionsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);

	/**
	 * Find active sessions without activity longer than specified time
	 * @param cutoffTime the cutoff time
	 * @return list of active UserSession objects without activity since cutoffTime
	 */
	@Query("SELECT s FROM UserSession s WHERE s.active = true AND s.lastActivity < :cutoffTime")
	List<UserSession> findActiveSessionsWithoutActivitySince(@Param("cutoffTime") LocalDateTime cutoffTime);

	/**
	 * Find all active sessions
	 * @return list of active UserSession objects
	 */
	List<UserSession> findByActiveTrue();

	/**
	 * Find all sessions (active and inactive) sorted by login time
	 * @return list of all UserSession objects sorted by login time descending
	 */
	List<UserSession> findAllByOrderByLoginTimeDesc();

	/**
	 * Find all sessions with pagination and sorting
	 * @return list of all UserSession objects sorted by login time descending
	 */
	@Query("SELECT s FROM UserSession s ORDER BY s.loginTime DESC")
	List<UserSession> findAllSessionsOrderByLoginTimeDesc();

	/**
	 * Find sessions by active status
	 * @param active the active status
	 * @return list of UserSession objects with specified active status, sorted by login time descending
	 */
	List<UserSession> findByActiveOrderByLoginTimeDesc(boolean active);

	/**
	 * Find inactive sessions (for cleanup)
	 * @param cutoffTime the cutoff time
	 * @return list of inactive UserSession objects older than cutoffTime
	 */
	@Query("SELECT s FROM UserSession s WHERE s.active = true AND s.lastActivity < :cutoffTime")
	List<UserSession> findInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

	/**
	 * Cleanup inactive sessions
	 * @param cutoffTime the cutoff time
	 * @param logoutTime the time of logout
	 * @return number of rows affected (number of sessions deactivated)
	 */
	@Modifying
	@Query("UPDATE UserSession s SET s.active = false, s.logoutTime = :logoutTime " +
			"WHERE s.active = true AND s.lastActivity < :cutoffTime")
	int cleanupInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime, @Param("logoutTime") LocalDateTime logoutTime);

	// Additional methods for SessionMonitoringService

	/**
	 * Count active sessions
	 * @return number of active sessions
	 */
	long countByActiveTrue();

	/**
	 * Count sessions created after specified time
	 * @param startTime the start time
	 * @return number of sessions created after startTime
	 */
	long countByLoginTimeAfter(LocalDateTime startTime);

	/**
	 * Get average session duration for completed sessions
	 * @return average session duration in minutes
	 */
	@Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (logout_time - login_time)) / 60.0) " +
			"FROM user_sessions WHERE active = false AND logout_time IS NOT NULL", 
			nativeQuery = true)
	Double getAverageSessionDuration();

	/**
	 * Get peak concurrent sessions since specified time (simplified)
	 * @param startTime the start time
	 * @return peak number of concurrent sessions since startTime
	 */
	@Query("SELECT COUNT(s.id) FROM UserSession s WHERE s.loginTime >= :startTime AND s.active = true")
	Long getPeakConcurrentSessions(@Param("startTime") LocalDateTime startTime);

	/**
	 * Get top active users by session count
	 * @param limit the maximum number of users to return
	 * @return list of maps containing userId and sessionCount
	 */
	@Query("SELECT s.userId as userId, COUNT(s.id) as sessionCount " +
			"FROM UserSession s WHERE s.active = true " +
			"GROUP BY s.userId ORDER BY COUNT(s.id) DESC")
	List<Map<String, Object>> getTopActiveUsers(@Param("limit") int limit);

	/**
	 * Get session activity by hour
	 * @param startTime the start time
	 * @return list of maps containing hour and count of sessions started in that hour
	 */
	@Query("SELECT EXTRACT(HOUR FROM s.loginTime) as hour, COUNT(s.id) as count " +
			"FROM UserSession s WHERE s.loginTime >= :startTime " +
			"GROUP BY EXTRACT(HOUR FROM s.loginTime) ORDER BY EXTRACT(HOUR FROM s.loginTime)")
	List<Map<String, Object>> getSessionActivityByHour(@Param("startTime") LocalDateTime startTime);

	/**
	 * Get users with excessive active sessions
	 * @param maxAllowed the maximum allowed active sessions per user
	 * @return list of maps containing userId and sessionCount for users exceeding maxAllowed
	 */
	@Query("SELECT s.userId as userId, COUNT(s.id) as sessionCount " +
			"FROM UserSession s WHERE s.active = true " +
			"GROUP BY s.userId HAVING COUNT(s.id) > :maxAllowed")
	List<Map<String, Object>> getUsersWithExcessiveSessions(@Param("maxAllowed") int maxAllowed);

	/**
	 * Get users with sessions from multiple IP addresses
	 * @return list of maps containing userId and ipCount for users with sessions from multiple IPs
	 */
	@Query("SELECT s.userId as userId, COUNT(DISTINCT s.ipAddress) as ipCount " +
			"FROM UserSession s WHERE s.active = true " +
			"GROUP BY s.userId HAVING COUNT(DISTINCT s.ipAddress) > 1")
	List<Map<String, Object>> getUsersWithMultipleIPs();

	/**
	 * Find recent sessions (limited to top 20)
	 * @return list of the 20 most recent UserSession objects sorted by login time descending
	 */
	List<UserSession> findTop20ByOrderByLoginTimeDesc();
}