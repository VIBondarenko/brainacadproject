package io.github.vibondarenko.clavionx.security;

/**
 * Enumeration of available two-factor authentication methods
 */
public enum TwoFactorMethod {
    EMAIL("Email"),
    PHONE("Phone/SMS"),
    BOTH("Email and Phone");

    private final String displayName;

    TwoFactorMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



