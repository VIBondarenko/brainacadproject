package com.brainacad.ecs.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for Security and Role management operations
 */
public final class SecurityUtils {
    
    private SecurityUtils() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Convert Role to Spring Security authorities
     * @param role User role
     * @return Collection of GrantedAuthority
     */
    public static Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return role.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
    
    /**
     * Convert multiple roles to Spring Security authorities
     * @param roles Set of user roles
     * @return Collection of GrantedAuthority
     */
    public static Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> role.getAuthorities().stream())
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
    
    /**
     * Check if user has specific permission
     * @param userDetails Spring Security UserDetails
     * @param permission Permission to check
     * @return true if user has permission
     */
    public static boolean hasPermission(UserDetails userDetails, Permission permission) {
        String requiredAuthority = permission.getAuthority();
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredAuthority));
    }
    
    /**
     * Check if user has specific role
     * @param userDetails Spring Security UserDetails
     * @param role Role to check
     * @return true if user has role
     */
    public static boolean hasRole(UserDetails userDetails, Role role) {
        String requiredAuthority = "ROLE_" + role.name();
        return userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredAuthority));
    }
    
    /**
     * Check if user has any of the specified roles
     * @param userDetails Spring Security UserDetails
     * @param roles Roles to check
     * @return true if user has any of the roles
     */
    public static boolean hasAnyRole(UserDetails userDetails, Role... roles) {
        for (Role role : roles) {
            if (hasRole(userDetails, role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if user has all of the specified roles
     * @param userDetails Spring Security UserDetails
     * @param roles Roles to check
     * @return true if user has all roles
     */
    public static boolean hasAllRoles(UserDetails userDetails, Role... roles) {
        for (Role role : roles) {
            if (!hasRole(userDetails, role)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get highest priority role for user (based on enum order)
     * @param userDetails Spring Security UserDetails
     * @return Highest priority role or null if no recognized roles
     */
    public static Role getHighestRole(UserDetails userDetails) {
        for (Role role : Role.values()) {
            if (hasRole(userDetails, role)) {
                return role;
            }
        }
        return null;
    }
    
    /**
     * Check if user is administrator (has any admin role)
     * @param userDetails Spring Security UserDetails
     * @return true if user has administrative privileges
     */
    public static boolean isAdministrator(UserDetails userDetails) {
        return hasAnyRole(userDetails, Role.SUPER_ADMIN, Role.ADMIN);
    }
    
    /**
     * Check if user can manage courses
     * @param userDetails Spring Security UserDetails
     * @return true if user can manage courses
     */
    public static boolean canManageCourses(UserDetails userDetails) {
        return hasAnyRole(userDetails, Role.SUPER_ADMIN, Role.ADMIN, Role.MANAGER, Role.TEACHER);
    }
    
    /**
     * Check if user can view analytics
     * @param userDetails Spring Security UserDetails
     * @return true if user can view analytics
     */
    public static boolean canViewAnalytics(UserDetails userDetails) {
        return hasAnyRole(userDetails, Role.SUPER_ADMIN, Role.ADMIN, Role.MANAGER, Role.ANALYST, Role.TEACHER);
    }
    
    /**
     * Get Spring Expression for role checking (for use in @PreAuthorize)
     * @param role Role to check
     * @return Spring EL expression
     */
    public static String roleExpression(Role role) {
        return "hasRole('" + role.name() + "')";
    }
    
    /**
     * Get Spring Expression for permission checking (for use in @PreAuthorize)
     * @param permission Permission to check
     * @return Spring EL expression
     */
    public static String permissionExpression(Permission permission) {
        return "hasAuthority('" + permission.getAuthority() + "')";
    }
    
    /**
     * Get Spring Expression for multiple roles (OR logic)
     * @param roles Roles to check
     * @return Spring EL expression
     */
    public static String anyRoleExpression(Role... roles) {
        if (roles.length == 0) return "false";
        if (roles.length == 1) return roleExpression(roles[0]);
        
        StringBuilder expr = new StringBuilder("hasAnyRole(");
        for (int i = 0; i < roles.length; i++) {
            if (i > 0) expr.append(", ");
            expr.append("'").append(roles[i].name()).append("'");
        }
        expr.append(")");
        return expr.toString();
    }
    
    /**
     * Get Spring Expression for multiple permissions (OR logic)
     * @param permissions Permissions to check
     * @return Spring EL expression
     */
    public static String anyPermissionExpression(Permission... permissions) {
        if (permissions.length == 0) return "false";
        if (permissions.length == 1) return permissionExpression(permissions[0]);
        
        StringBuilder expr = new StringBuilder("hasAnyAuthority(");
        for (int i = 0; i < permissions.length; i++) {
            if (i > 0) expr.append(", ");
            expr.append("'").append(permissions[i].getAuthority()).append("'");
        }
        expr.append(")");
        return expr.toString();
    }
}
