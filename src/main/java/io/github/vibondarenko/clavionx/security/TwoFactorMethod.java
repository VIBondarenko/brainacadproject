package io.github.vibondarenko.clavionx.security;

/**
 * Enumeration of available two-factor authentication methods
 * with user-friendly display names.
 * Each method represents a different way to verify user identity.
 * EMAIL: Verification via email.
 * PHONE: Verification via phone/SMS.
 * BOTH: Verification via both email and phone/SMS.
 */
public enum TwoFactorMethod {
    EMAIL("Email"),
    PHONE("Phone/SMS"),
    BOTH("Email and Phone");

    private final String displayName;

    /**
     * Constructor for TwoFactorMethod enum
     * @param displayName
     */
    TwoFactorMethod(String displayName) {
        this.displayName = displayName;
    }
    /**
     * Get the user-friendly display name of the two-factor method
     * @return display name
     */
    public String getDisplayName() {
        return displayName;
    }
}



