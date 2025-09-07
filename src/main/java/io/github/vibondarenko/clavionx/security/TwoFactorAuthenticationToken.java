package io.github.vibondarenko.clavionx.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Custom authentication token for two-factor authentication process
 * Represents a partially authenticated user who needs to provide 2FA code
 */
public class TwoFactorAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String username;
    private final boolean requiresTwoFactor;

    /**
     * Constructor for unauthenticated token (before 2FA verification)
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
     */
    public TwoFactorAuthenticationToken(Object principal, String username, 
                                      Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.username = username;
        this.requiresTwoFactor = false;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials needed for 2FA token
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getUsername() {
        return username;
    }

    public boolean requiresTwoFactor() {
        return requiresTwoFactor;
    }

    @Override
    public String toString() {
        return "TwoFactorAuthenticationToken{" +
                "username='" + username + '\'' +
                ", requiresTwoFactor=" + requiresTwoFactor +
                ", authenticated=" + isAuthenticated() +
                '}';
    }
}



