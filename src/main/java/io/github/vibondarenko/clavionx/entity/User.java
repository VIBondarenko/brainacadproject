package io.github.vibondarenko.clavionx.entity;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import io.github.vibondarenko.clavionx.security.Permission;
import io.github.vibondarenko.clavionx.security.Role;
import io.github.vibondarenko.clavionx.security.SecurityUtils;
import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User entity for authentication and authorization
 * Base class for all user types (Student, Trainer, Analyst, etc.)
 */
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User extends Person implements UserDetails {

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    private boolean credentialsNonExpired = true;

    @Column(name = "avatar_path", length = 255)
    private String avatarPath;

    // Two-Factor Authentication fields
    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_two_factor_method", length = 20)
    private TwoFactorMethod preferredTwoFactorMethod;

    // Constructors
    protected User() {
        super();
    }
    
    public User(String name, String lastName, String username, String password, String email, Role role) {
        super(name, lastName, email);
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
    }

    public User(String name, String lastName, Integer age, String username, String password, String email, Role role) {
        super(name, lastName, email, age);
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = true;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
    }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return SecurityUtils.getAuthorities(role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // Getters and Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    // Business methods
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    public boolean hasPermission(Permission permission) {
        return role.hasPermission(permission);
    }

    public boolean hasPermissionByName(String permissionName) {
        try {
            Permission permission = Permission.valueOf(permissionName.toUpperCase());
            return role.hasPermission(permission);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    // Two-Factor Authentication getters and setters
    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public TwoFactorMethod getPreferredTwoFactorMethod() {
        return preferredTwoFactorMethod;
    }

    public void setPreferredTwoFactorMethod(TwoFactorMethod preferredTwoFactorMethod) {
        this.preferredTwoFactorMethod = preferredTwoFactorMethod;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\tUsername: " + username + "\n" +
                "\tRole: " + role + "\n" +
                "\tEnabled: " + enabled + "\n";
    }

    /**
     * Checks equality based on username, email and role.
     * @param o object to compare
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        if (!super.equals(o)) return false;
        return username != null && username.equals(user.username)
                && getEmail() != null && getEmail().equals(user.getEmail())
                && role == user.role;
    }

    /**
     * Hash code based on username, email and role.
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (getEmail() != null ? getEmail().hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        return result;
    }
}