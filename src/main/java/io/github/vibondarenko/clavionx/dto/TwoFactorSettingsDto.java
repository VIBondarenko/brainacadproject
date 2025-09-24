package io.github.vibondarenko.clavionx.dto;

import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for Two-Factor Authentication settings
 */
public class TwoFactorSettingsDto {

    private boolean twoFactorEnabled;

    @NotNull(message = "Two-factor method is required")
    private TwoFactorMethod preferredMethod;

    private boolean hasPhoneNumber;
    private boolean hasEmail;

    // Constructors
    public TwoFactorSettingsDto() {}

    public TwoFactorSettingsDto(boolean twoFactorEnabled, TwoFactorMethod preferredMethod, 
                                boolean hasPhoneNumber, boolean hasEmail) {
        this.twoFactorEnabled = twoFactorEnabled;
        this.preferredMethod = preferredMethod;
        this.hasPhoneNumber = hasPhoneNumber;
        this.hasEmail = hasEmail;
    }

    // Getters and Setters
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public TwoFactorMethod getPreferredMethod() {
        return preferredMethod;
    }

    public void setPreferredMethod(TwoFactorMethod preferredMethod) {
        this.preferredMethod = preferredMethod;
    }

    public boolean isHasPhoneNumber() {
        return hasPhoneNumber;
    }

    public void setHasPhoneNumber(boolean hasPhoneNumber) {
        this.hasPhoneNumber = hasPhoneNumber;
    }

    public boolean isHasEmail() {
        return hasEmail;
    }

    public void setHasEmail(boolean hasEmail) {
        this.hasEmail = hasEmail;
    }

    // Helper methods
    public boolean canUsePhone() {
        return hasPhoneNumber;
    }

    public boolean canUseEmail() {
        return hasEmail;
    }

    public boolean canUseBoth() {
        return hasPhoneNumber && hasEmail;
    }
}