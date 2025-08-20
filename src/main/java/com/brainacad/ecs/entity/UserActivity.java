package com.brainacad.ecs.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity for tracking user activities and audit trail
 * Provides comprehensive logging of user actions for security and analytics
 */
@Entity
@Table(name = "user_activities", indexes = {
    @Index(name = "idx_user_activities_user_id", columnList = "user_id"),
    @Index(name = "idx_user_activities_timestamp", columnList = "timestamp"),
    @Index(name = "idx_user_activities_action_type", columnList = "action_type")
})
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User ID is required")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "Username is required")
    @Size(max = 100, message = "Username cannot exceed 100 characters")
    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @NotNull(message = "Timestamp is required")
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @NotBlank(message = "Action type is required")
    @Size(max = 50, message = "Action type cannot exceed 50 characters")
    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Size(max = 255, message = "Action description cannot exceed 255 characters")
    @Column(name = "action_description", length = 255)
    private String actionDescription;

    @Size(max = 100, message = "Resource type cannot exceed 100 characters")
    @Column(name = "resource_type", length = 100)
    private String resourceType;

    @Column(name = "resource_id")
    private Long resourceId;

    @Size(max = 45, message = "IP address cannot exceed 45 characters")
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Size(max = 500, message = "User agent cannot exceed 500 characters")
    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Size(max = 20, message = "Status cannot exceed 20 characters")
    @Column(name = "status", length = 20)
    private String status; // SUCCESS, FAILURE, ERROR

    @Column(name = "details", columnDefinition = "TEXT")
    private String details; // JSON or detailed information

    // Constructors
    public UserActivity() {
        this.timestamp = LocalDateTime.now();
        this.status = "SUCCESS";
    }

    public UserActivity(Long userId, String username, String actionType, String actionDescription) {
        this();
        this.userId = userId;
        this.username = username;
        this.actionType = actionType;
        this.actionDescription = actionDescription;
    }

    // Builder pattern for easy creation
    public static UserActivity.Builder builder() {
        return new UserActivity.Builder();
    }

    public static class Builder {
        private UserActivity activity = new UserActivity();

        public Builder userId(Long userId) {
            activity.userId = userId;
            return this;
        }

        public Builder username(String username) {
            activity.username = username;
            return this;
        }

        public Builder actionType(String actionType) {
            activity.actionType = actionType;
            return this;
        }

        public Builder actionDescription(String actionDescription) {
            activity.actionDescription = actionDescription;
            return this;
        }

        public Builder resourceType(String resourceType) {
            activity.resourceType = resourceType;
            return this;
        }

        public Builder resourceId(Long resourceId) {
            activity.resourceId = resourceId;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            activity.ipAddress = ipAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            activity.userAgent = userAgent;
            return this;
        }

        public Builder status(String status) {
            activity.status = status;
            return this;
        }

        public Builder details(String details) {
            activity.details = details;
            return this;
        }

        public UserActivity build() {
            return activity;
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "id=" + id +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                ", timestamp=" + timestamp +
                ", actionType='" + actionType + '\'' +
                ", actionDescription='" + actionDescription + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", resourceId=" + resourceId +
                ", status='" + status + '\'' +
                '}';
    }
}
