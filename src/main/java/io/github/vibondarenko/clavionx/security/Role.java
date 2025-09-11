package io.github.vibondarenko.clavionx.security;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * System roles for Education Management System
 * Represents hierarchical role-based access control
 */
public enum Role {
    /**
     * System superuser with full access to everything
     */
    SUPER_ADMIN("SUPER_ADMIN", "Super Administrator", Set.of(
        Permission.SYSTEM_MANAGE,
        Permission.USER_MANAGE_ALL,
        Permission.COURSE_MANAGE_ALL,
        Permission.ANALYTICS_VIEW_ALL,
        Permission.SETTINGS_MANAGE,
        Permission.BACKUP_MANAGE,
        Permission.AUDIT_VIEW
    )),
    
    /**
     * Administrator with full educational content management
     */
    ADMIN("ADMIN", "Administrator", Set.of(
        Permission.COURSE_MANAGE_ALL,
        Permission.STUDENT_MANAGE_ALL,
        Permission.TEACHER_MANAGE_ALL,
        Permission.ANALYTICS_VIEW_ALL,
        Permission.REPORTS_GENERATE,
        Permission.SCHEDULE_MANAGE
    )),
    
    /**
     * Educational manager - course and user management without technical settings
     */
    MANAGER("MANAGER", "Manager", Set.of(
        Permission.COURSE_CREATE,
        Permission.COURSE_EDIT_ALL,
        Permission.TEACHER_ASSIGN,
        Permission.STUDENT_VIEW_ALL,
        Permission.ANALYTICS_VIEW_LIMITED,
        Permission.REPORTS_VIEW,
        Permission.COMMUNICATION_SEND
    )),
    
    /**
     * Teacher/Trainer - manages own courses and students
     */
    TEACHER("TEACHER", "Teacher", Set.of(
        Permission.COURSE_CREATE_OWN,
        Permission.COURSE_EDIT_OWN,
        Permission.STUDENT_MANAGE_OWN,
        Permission.TASK_MANAGE_OWN,
        Permission.GRADE_MANAGE_OWN,
        Permission.ANALYTICS_VIEW_OWN,
        Permission.REPORTS_VIEW_OWN
    )),
    
    /**
     * Data analyst - read-only access to analytics and reports
     */
    ANALYST("ANALYST", "Analyst", Set.of(
        Permission.ANALYTICS_VIEW_ALL,
        Permission.REPORTS_GENERATE,
        Permission.REPORTS_VIEW,
        Permission.DATA_EXPORT,
        Permission.STATISTICS_VIEW
    )),
    
    /**
     * Content and user moderator
     */
    MODERATOR("MODERATOR", "Moderator", Set.of(
        Permission.CONTENT_MODERATE,
        Permission.STUDENT_APPLICATIONS_MANAGE,
        Permission.SUPPORT_PROVIDE,
        Permission.COMMUNICATION_MODERATE,
        Permission.REPORTS_VIEW
    )),
    
    /**
     * Student - access to own educational content
     */
    STUDENT("STUDENT", "Student", Set.of(
        Permission.COURSE_VIEW,
        Permission.COURSE_ENROLL,
        Permission.TASK_SUBMIT,
        Permission.GRADE_VIEW_OWN,
        Permission.PROGRESS_VIEW_OWN,
        Permission.PROFILE_EDIT_OWN
    )),
    
    /**
     * Guest user - limited read-only access
     */
    GUEST("GUEST", "Guest", Set.of(
        Permission.COURSE_VIEW_PUBLIC,
        Permission.REGISTRATION_REQUEST
    ));
    
    private final String name;
    private final String displayName;
    private final Set<Permission> permissions;
    
    Role(String name, String displayName, Set<Permission> permissions) {
        this.name = name;
        this.displayName = displayName;
        this.permissions = permissions;
    }
    
    public String getName() {
        return name;
    }   

    public String getDisplayName() {
        return displayName;
    }
    
    public Set<Permission> getPermissions() {
        return permissions;
    }
    
    /**
     * Get all Spring Security authorities for this role
     * @return Set of authority strings
     */
    public Set<String> getAuthorities() {
        Set<String> authorities = permissions.stream()
                .map(permission -> "PERMISSION_" + permission.name())
                .collect(Collectors.toSet());
        
        // Add role authority
        authorities.add("ROLE_" + this.name());
        
        return authorities;
    }
    
    /**
     * Check if this role has specific permission
     * @param permission Permission to check
     * @return true if role has permission
     */
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }
    
    /**
     * Check if this role has higher or equal priority than another role
     * @param other Role to compare with
     * @return true if this role has higher or equal priority
     */
    public boolean hasHigherOrEqualPriorityThan(Role other) {
        return this.ordinal() <= other.ordinal();
    }
    
    /**
     * Get all administrative roles
     * @return Set of administrative roles
     */
    public static String [] getAdministrativeRoles() {
        return new String[] {SUPER_ADMIN.name(), ADMIN.name()};
    }
    
    /**
     * Get all roles with dashboard access
     * @return Set of roles with dashboard access
     */    
    public static String[] getDashboardAccessRoles() {
        return new String[] {SUPER_ADMIN.name(), ADMIN.name(), MANAGER.name(), TEACHER.name(), ANALYST.name(), MODERATOR.name(), STUDENT.name()};
    }

    /**
     * Get all educational roles (who work with courses directly)
     * @return Set of educational roles
     */
    public static String[] getEducationalRoles() {
        return new String[] {ADMIN.name(), MANAGER.name(), TEACHER.name(), STUDENT.name()};
    }
    
    /**
     * Get roles that can manage courses
     * @return Set of roles with course management permissions
     */
    public static String[] getCourseManagementRoles() {
        return new String[] {SUPER_ADMIN.name(), ADMIN.name(), MANAGER.name(), TEACHER.name()};
    }

    /**
     * Get roles that can view courses
     * @return Set of roles with course viewing permissions
     */
    public static String[] getCourseViewingRoles() {
        return new String[] {SUPER_ADMIN.name(), ADMIN.name(), MANAGER.name(), TEACHER.name(), STUDENT.name()};
    }

    /**
     * Get all roles with analytics access
     * @return Set of roles with analytics access
     */
    public static String[] getAnalyticsAccessRoles() {
        return new String[] {SUPER_ADMIN.name(), ADMIN.name(), MANAGER.name(), ANALYST.name()};
    }

    /**
     * Get all roles with student area access
     * @return Set of roles with student area access
     */
    public static String[] getStudentAreaAccessRoles() {
        return new String[] {SUPER_ADMIN.name(), ADMIN.name(), MANAGER.name(), TEACHER.name(), STUDENT.name()};
    }

}



