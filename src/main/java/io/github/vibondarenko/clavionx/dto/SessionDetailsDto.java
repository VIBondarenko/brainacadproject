package io.github.vibondarenko.clavionx.dto;

import java.time.LocalDateTime;

import io.github.vibondarenko.clavionx.entity.UserSession;

/**
 * Data Transfer Object for session details with username information
 */
public class SessionDetailsDto {
    
    private Long id;
    private String sessionId;
    private Long userId;
    private String username;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private String ipAddress;
    private String userAgent;
    private String deviceInfo;
    private boolean active;
    private LocalDateTime logoutTime;

    // Constructors
    public SessionDetailsDto() {
    }

    public SessionDetailsDto(UserSession session, String username) {
        this.id = session.getId();
        this.sessionId = session.getSessionId();
        this.userId = session.getUserId();
        this.username = username;
        this.loginTime = session.getLoginTime();
        this.lastActivity = session.getLastActivity();
        this.ipAddress = session.getIpAddress();
        this.userAgent = session.getUserAgent();
        this.deviceInfo = session.getDeviceInfo();
        this.active = session.isActive();
        this.logoutTime = session.getLogoutTime();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}



