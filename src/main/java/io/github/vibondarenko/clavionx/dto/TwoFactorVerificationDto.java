package io.github.vibondarenko.clavionx.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for Two-Factor Authentication verification
 */
public class TwoFactorVerificationDto {

    @NotBlank(message = "Verification code is required")
    @Size(min = 6, max = 6, message = "Verification code must be exactly 6 digits")
    @Pattern(regexp = "\\d{6}", message = "Verification code must contain only digits")
    private String verificationCode;

    private boolean rememberDevice = false;

    // Constructors
    public TwoFactorVerificationDto() {}

    public TwoFactorVerificationDto(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public TwoFactorVerificationDto(String verificationCode, boolean rememberDevice) {
        this.verificationCode = verificationCode;
        this.rememberDevice = rememberDevice;
    }

    // Getters and Setters
    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean isRememberDevice() {
        return rememberDevice;
    }

    public void setRememberDevice(boolean rememberDevice) {
        this.rememberDevice = rememberDevice;
    }

    @Override
    public String toString() {
        return "TwoFactorVerificationDto{" +
                "verificationCode='" + (verificationCode != null ? "***" : "null") + '\'' +
                ", rememberDevice=" + rememberDevice +
                '}';
    }
}



