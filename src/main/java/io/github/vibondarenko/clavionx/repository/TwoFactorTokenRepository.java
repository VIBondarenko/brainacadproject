package io.github.vibondarenko.clavionx.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.TwoFactorToken;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.security.TwoFactorMethod;

/**
 * Repository interface for TwoFactorToken entity
 * @see TwoFactorToken
 * @see User
 * @see TwoFactorMethod
 */
@Repository
public interface TwoFactorTokenRepository extends JpaRepository<TwoFactorToken, Long> {

	/**
	 * Find active (unused and not expired) token for user and verification code
     * with attempts less than maxAttempts
     * @param user the user to whom the token belongs
     * @param code the verification code of the token
     * @param now the current time to check for expiration
     * @return an Optional containing the found TwoFactorToken, or empty if none found
	 */
	@Query("SELECT t FROM TwoFactorToken t WHERE t.user = :user AND t.verificationCode = :code " +
		"AND t.used = false AND t.expiresAt > :now AND t.attempts < t.maxAttempts")
	Optional<TwoFactorToken> findActiveTokenByUserAndCode(
		@Param("user") User user, 
		@Param("code") String code, 
		@Param("now") LocalDateTime now
	);

	/**
	 * Find all active tokens for a specific user
     * @param user the user to whom the tokens belong
     * @param now the current time to check for expiration
     * @return a list of active TwoFactorTokens for the user
	 */
	@Query("SELECT t FROM TwoFactorToken t WHERE t.user = :user " +
		"AND t.used = false AND t.expiresAt > :now AND t.attempts < t.maxAttempts")
	List<TwoFactorToken> findActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

	/**
	 * Find the most recent token for user and method
     * @param user the user to whom the token belongs
     * @param method the two-factor authentication method
     * @return a list of TwoFactorTokens for the user and method, ordered by creation
	 */
	@Query("SELECT t FROM TwoFactorToken t WHERE t.user = :user AND t.method = :method " +
		"ORDER BY t.createdAt DESC")
	List<TwoFactorToken> findTokensByUserAndMethod(
		@Param("user") User user, 
		@Param("method") TwoFactorMethod method
	);

	/**
	 * Find tokens by user ordered by creation date (most recent first)
     * @param user the user to whom the tokens belong
     * @return a list of TwoFactorTokens for the user, ordered by creation date descending
	 */
	List<TwoFactorToken> findByUserOrderByCreatedAtDesc(User user);

	/**
	 * Check if user has any active tokens
     * @param user the user to check for active tokens
     * @param now the current time to check for expiration
     * @return true if the user has active tokens, false otherwise
	 */
	@Query("SELECT COUNT(t) > 0 FROM TwoFactorToken t WHERE t.user = :user " +
		"AND t.used = false AND t.expiresAt > :now AND t.attempts < t.maxAttempts")
	boolean hasActiveTokens(@Param("user") User user, @Param("now") LocalDateTime now);

	/**
	 * Delete all expired tokens (cleanup task)
     * @param now the current time to check for expiration
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM TwoFactorToken t WHERE t.expiresAt < :now")
	void deleteExpiredTokens(@Param("now") LocalDateTime now);

	/**
	 * Delete all used tokens older than specified date
     * @param before the cutoff date; tokens used before this date will be deleted
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM TwoFactorToken t WHERE t.used = true AND t.usedAt < :before")
	void deleteUsedTokensOlderThan(@Param("before") LocalDateTime before);

	/**
	 * Mark all active tokens for user as used (for security reasons)
     * @param user the user whose tokens should be invalidated
     * @param now the current time to set as usedAt timestamp
     * 
	 */
	@Modifying
	@Transactional
	@Query("UPDATE TwoFactorToken t SET t.used = true, t.usedAt = :now WHERE t.user = :user " +
		"AND t.used = false AND t.expiresAt > :now")
	void invalidateActiveTokensForUser(@Param("user") User user, @Param("now") LocalDateTime now);

	/**
	 * Count active tokens for a user
     * @param user the user to count active tokens for
     * @param now the current time to check for expiration
     * @return the count of active tokens for the user
	 */
	@Query("SELECT COUNT(t) FROM TwoFactorToken t WHERE t.user = :user " +
		"AND t.used = false AND t.expiresAt > :now AND t.attempts < t.maxAttempts")
	long countActiveTokensByUser(@Param("user") User user, @Param("now") LocalDateTime now);

	/**
	 * Find tokens that need cleanup (expired or used tokens older than specified days)
     * @param now the current time to check for expiration
     * @param cleanupBefore the cutoff date; used tokens before this date will be selected for cleanup
     * @return a list of TwoFactorTokens that are eligible for cleanup
	 */
	@Query("SELECT t FROM TwoFactorToken t WHERE " +
		"(t.expiresAt < :now) OR " +
		"(t.used = true AND t.usedAt < :cleanupBefore)")
	List<TwoFactorToken> findTokensForCleanup(
		@Param("now") LocalDateTime now, 
		@Param("cleanupBefore") LocalDateTime cleanupBefore
	);
}

