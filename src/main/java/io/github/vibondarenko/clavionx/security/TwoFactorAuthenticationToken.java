package io.github.vibondarenko.clavionx.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Custom authentication token for two-factor authentication process
 * Represents a partially authenticated user who needs to provide 2FA code
 */
public class TwoFactorAuthenticationToken extends AbstractAuthenticationToken {

    private final transient Object principal;
    private final String username;
    private final boolean requiresTwoFactor;

    /**
     * Constructor for unauthenticated token (before 2FA verification)
     * @param username the username of the user attempting to authenticate
     */
    public TwoFactorAuthenticationToken(String username) {
        super(null);
        this.principal = username;
        this.username = username;
        this.requiresTwoFactor = true;
        setAuthenticated(false);
    }

    /**
     * Constructor for authenticated token (after successful 2FA verification)
     * @param principal the authenticated user principal
     * @param username the username of the authenticated user
     * @param authorities the granted authorities for the authenticated user
     */
    public TwoFactorAuthenticationToken(Object principal, String username, 
                                        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.username = username;
        this.requiresTwoFactor = false;
        setAuthenticated(true);
    }
    /**
     * Returns the credentials associated with this authentication token.
     * For 2FA token, credentials are not needed, so this returns null.
     * @return null since no credentials are needed for 2FA token
     */
    @Override
    public Object getCredentials() {
        return null; // No credentials needed for 2FA token
    }
    /**
     * Returns the principal associated with this authentication token.
     * The principal can be the username or the authenticated user object.
     * @return the principal (username or user object)
     */
    @Override
    public Object getPrincipal() {
        return principal;
    }
    /**
     * Returns the username associated with this authentication token.
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }
    /**
     * Indicates whether this token requires two-factor authentication.
     * @return true if 2FA is required, false otherwise
     */
    public boolean requiresTwoFactor() {
        return requiresTwoFactor;
    }
    /**
     * Provides a string representation of the TwoFactorAuthenticationToken.
     * Includes username, 2FA requirement status, and authentication status.
     * @return string representation of the token
     */
    @Override
    public String toString() {
        return "TwoFactorAuthenticationToken{" +
                "username='" + username + '\'' +
                ", requiresTwoFactor=" + requiresTwoFactor +
                ", authenticated=" + isAuthenticated() +
                '}';
    }
    /**
     * Compares this token to another object for equality.
     * Tokens are considered equal if they have the same principal, username,
     * 2FA requirement status, and granted authorities.
     * @param obj the object to compare with
     * @return true if the tokens are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TwoFactorAuthenticationToken)) return false;
        TwoFactorAuthenticationToken that = (TwoFactorAuthenticationToken) obj;
        return requiresTwoFactor == that.requiresTwoFactor &&
                java.util.Objects.equals(principal, that.principal) &&
                java.util.Objects.equals(username, that.username) &&
                java.util.Objects.equals(getAuthorities(), that.getAuthorities());
    }
    /**
     * Computes the hash code for this token based on its principal, username,
     * 2FA requirement status, and granted authorities.
     * @return the computed hash code
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(principal, username, requiresTwoFactor, getAuthorities());
    }
}



