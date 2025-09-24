package io.github.vibondarenko.clavionx.entity;

import java.time.LocalDateTime;

import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity representing a two-factor authentication token
 * Used for temporary verification codes sent via email or SMS
 */
@Entity
@Table(name = "two_factor_tokens")
public class TwoFactorToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Verification code cannot be blank")
    @Size(min = 6, max = 6, message = "Verification code must be exactly 6 characters")
    @Column(name = "verification_code", nullable = false, length = 6)
    private String verificationCode;

    @NotNull(message = "Two-factor method cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private TwoFactorMethod method;

    @NotNull(message = "User cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Expiry time cannot be null")
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @Column(name = "attempts", nullable = false)
    private int attempts = 0;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts = 3;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    // Default constructor
    public TwoFactorToken() {}

    // Constructor for creating new tokens
    public TwoFactorToken(String verificationCode, TwoFactorMethod method, User user, LocalDateTime expiresAt) {
        this.verificationCode = verificationCode;
        this.method = method;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public TwoFactorMethod getMethod() {
        return method;
    }

    public void setMethod(TwoFactorMethod method) {
        this.method = method;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
        if (used && usedAt == null) {
            this.usedAt = LocalDateTime.now();
        }
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired() && attempts < maxAttempts;
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public boolean hasExceededMaxAttempts() {
        return attempts >= maxAttempts;
    }

    @Override
    public String toString() {
        return "TwoFactorToken{" +
                "id=" + id +
                ", method=" + method +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", expiresAt=" + expiresAt +
                ", used=" + used +
                ", attempts=" + attempts +
                ", createdAt=" + createdAt +
                '}';
    }
}