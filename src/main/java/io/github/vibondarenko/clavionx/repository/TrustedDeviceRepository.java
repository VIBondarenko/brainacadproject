package io.github.vibondarenko.clavionx.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.TrustedDevice;
import io.github.vibondarenko.clavionx.entity.User;

/**
 * Repository for managing trusted devices
 */
@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

	/**
	 * Find active and valid trusted device by user and device identifier
     * (used during authentication to verify if the device is trusted)
     * @param user the user to whom the device belongs
     * @param deviceIdentifier the unique identifier of the device
     * @param now the current time to check for expiration
     * @return an Optional containing the trusted device if found and valid, otherwise empty
	 */
	@Query("SELECT td FROM TrustedDevice td WHERE td.user = :user AND td.deviceIdentifier = :deviceIdentifier " +
			"AND td.active = true AND td.expiresAt > :now")
	Optional<TrustedDevice> findValidTrustedDevice(@Param("user") User user, 
													@Param("deviceIdentifier") String deviceIdentifier,
													@Param("now") LocalDateTime now);

	/**
	 * Find all active trusted devices for a user
     * (used to display a list of trusted devices in user settings)
     * @param user the user whose active devices are to be retrieved
     * @return a list of active trusted devices for the user, ordered by last used date descending
	 */
	@Query("SELECT td FROM TrustedDevice td WHERE td.user = :user AND td.active = true " +
			"ORDER BY td.lastUsed DESC")
	List<TrustedDevice> findActiveDevicesByUser(@Param("user") User user);

	/**
	 * Find all valid (active and not expired) trusted devices for a user
     * (used to display a list of currently valid trusted devices in user settings)
     * @param user the user whose valid devices are to be retrieved
     * @param now the current time to check for expiration
     * @return a list of valid trusted devices for the user, ordered by last used date descending
	 */
	@Query("SELECT td FROM TrustedDevice td WHERE td.user = :user AND td.active = true " +
			"AND td.expiresAt > :now ORDER BY td.lastUsed DESC")
	List<TrustedDevice> findValidDevicesByUser(@Param("user") User user, @Param("now") LocalDateTime now);

	/**
	 * Find trusted device by user and device identifier (regardless of status)
     * (used to check if a device is already registered for a user)
     * @param user the user to whom the device belongs
     * @param deviceIdentifier the unique identifier of the device
     * @return an Optional containing the trusted device if found, otherwise empty
	 */
	Optional<TrustedDevice> findByUserAndDeviceIdentifier(User user, String deviceIdentifier);

	/**
	 * Count active trusted devices for user
     * (used to enforce limits on the number of trusted devices per user)
     * @param user the user whose active devices are to be counted
     * @return the count of active trusted devices for the user
	 */
	@Query("SELECT COUNT(td) FROM TrustedDevice td WHERE td.user = :user AND td.active = true")
	Long countActiveDevicesByUser(@Param("user") User user);

	/**
	 * Delete expired devices
     * (used to clean up old trusted devices that are no longer valid)
     * @param now the current time to check for expiration
	 */
	@Modifying
	@Query("DELETE FROM TrustedDevice td WHERE td.expiresAt < :now")
	void deleteExpiredDevices(@Param("now") LocalDateTime now);

	/**
	 * Deactivate all devices for user (useful for security purposes)
     * (used when a user wants to revoke all trusted devices, e.g., after a security incident)
     * @param user the user whose devices are to be deactivated
	 */
	@Modifying
	@Query("UPDATE TrustedDevice td SET td.active = false WHERE td.user = :user")
	void deactivateAllUserDevices(@Param("user") User user);

	/**
	 * Update last used timestamp for device
     * (used to track when a trusted device was last used for authentication)
     * @param deviceId the ID of the device to update
     * @param lastUsed the new last used timestamp
	 */
	@Modifying
	@Query("UPDATE TrustedDevice td SET td.lastUsed = :lastUsed WHERE td.id = :deviceId")
	void updateLastUsed(@Param("deviceId") Long deviceId, @Param("lastUsed") LocalDateTime lastUsed);
}