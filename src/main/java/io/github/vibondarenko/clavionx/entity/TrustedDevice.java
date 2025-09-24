package io.github.vibondarenko.clavionx.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entity representing a trusted device for 2FA bypass
 */
@Entity
@Table(name = "trusted_devices")
public class TrustedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "device_identifier", nullable = false, length = 255)
    private String deviceIdentifier;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "trusted_at", nullable = false)
    private LocalDateTime trustedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    // Constructors
    public TrustedDevice() {
        this.trustedAt = LocalDateTime.now();
        this.lastUsed = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(30);
    }

    public TrustedDevice(User user, String deviceIdentifier, String deviceName, 
                        String userAgent, String ipAddress) {
        this();
        this.user = user;
        this.deviceIdentifier = deviceIdentifier;
        this.deviceName = deviceName;
        this.userAgent = userAgent;
        this.ipAddress = ipAddress;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getTrustedAt() {
        return trustedAt;
    }

    public void setTrustedAt(LocalDateTime trustedAt) {
        this.trustedAt = trustedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return active && !isExpired();
    }

    public void updateLastUsed() {
        this.lastUsed = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TrustedDevice{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", deviceIdentifier='" + deviceIdentifier + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", trustedAt=" + trustedAt +
                ", expiresAt=" + expiresAt +
                ", active=" + active +
                '}';
    }
}