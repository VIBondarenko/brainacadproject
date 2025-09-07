package com.brainacad.ecs.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brainacad.ecs.entity.TrustedDevice;
import com.brainacad.ecs.entity.User;

/**
 * Repository for managing trusted devices
 */
@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, Long> {

    /**
     * Find active and valid trusted device by user and device identifier
     */
    @Query("SELECT td FROM TrustedDevice td WHERE td.user = :user AND td.deviceIdentifier = :deviceIdentifier " +
            "AND td.active = true AND td.expiresAt > :now")
    Optional<TrustedDevice> findValidTrustedDevice(@Param("user") User user, 
                                                    @Param("deviceIdentifier") String deviceIdentifier,
                                                    @Param("now") LocalDateTime now);

    /**
     * Find all active trusted devices for a user
     */
    @Query("SELECT td FROM TrustedDevice td WHERE td.user = :user AND td.active = true " +
            "ORDER BY td.lastUsed DESC")
    List<TrustedDevice> findActiveDevicesByUser(@Param("user") User user);

    /**
     * Find all valid (active and not expired) trusted devices for a user
     */
    @Query("SELECT td FROM TrustedDevice td WHERE td.user = :user AND td.active = true " +
            "AND td.expiresAt > :now ORDER BY td.lastUsed DESC")
    List<TrustedDevice> findValidDevicesByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Find trusted device by user and device identifier (regardless of status)
     */
    Optional<TrustedDevice> findByUserAndDeviceIdentifier(User user, String deviceIdentifier);

    /**
     * Count active trusted devices for user
     */
    @Query("SELECT COUNT(td) FROM TrustedDevice td WHERE td.user = :user AND td.active = true")
    Long countActiveDevicesByUser(@Param("user") User user);

    /**
     * Delete expired devices
     */
    @Modifying
    @Query("DELETE FROM TrustedDevice td WHERE td.expiresAt < :now")
    void deleteExpiredDevices(@Param("now") LocalDateTime now);

    /**
     * Deactivate all devices for user (useful for security purposes)
     */
    @Modifying
    @Query("UPDATE TrustedDevice td SET td.active = false WHERE td.user = :user")
    void deactivateAllUserDevices(@Param("user") User user);

    /**
     * Update last used timestamp for device
     */
    @Modifying
    @Query("UPDATE TrustedDevice td SET td.lastUsed = :lastUsed WHERE td.id = :deviceId")
    void updateLastUsed(@Param("deviceId") Long deviceId, @Param("lastUsed") LocalDateTime lastUsed);
}
