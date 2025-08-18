package com.brainacad.ecs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity для отслеживания пользовательских сессий
 */
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 255)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "device_info", length = 255)
    private String deviceInfo;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    // Constructors
    public UserSession() {
    }

    public UserSession(String sessionId, Long userId, String ipAddress, String userAgent) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.loginTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
        this.active = true;
        this.deviceInfo = extractDeviceInfo(userAgent);
    }

    // Helper method to extract device info from user agent
    private String extractDeviceInfo(String userAgent) {
        if (userAgent == null) return "Unknown";
        
        if (userAgent.contains("Mobile") || userAgent.contains("Android")) {
            return "Mobile";
        } else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return "iOS Device";
        } else if (userAgent.contains("Windows")) {
            return "Windows PC";
        } else if (userAgent.contains("Mac")) {
            return "Mac";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        }
        return "Desktop";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", userId=" + userId +
                ", loginTime=" + loginTime +
                ", lastActivity=" + lastActivity +
                ", ipAddress='" + ipAddress + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", active=" + active +
                '}';
    }
}
