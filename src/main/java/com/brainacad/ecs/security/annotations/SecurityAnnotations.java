package com.brainacad.ecs.security.annotations;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom security annotations for easier role-based access control
 */
public class SecurityAnnotations {
    
    /**
     * Allows access only to Super Administrators
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public @interface SuperAdminOnly {
    }
    
    /**
     * Allows access to Administrators and Super Administrators
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public @interface AdminOnly {
    }
    
    /**
     * Allows access to administrative roles (Super Admin, Admin, Manager)
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public @interface AdministrativeAccess {
    }
    
    /**
     * Allows access to educational staff (Admin, Manager, Teacher)
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public @interface EducationalStaff {
    }
    
    /**
     * Allows access to Teachers only
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('TEACHER')")
    public @interface TeacherOnly {
    }
    
    /**
     * Allows access to Students only
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('STUDENT')")
    public @interface StudentOnly {
    }
    
    /**
     * Allows access to Analysts only
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasRole('ANALYST')")
    public @interface AnalystOnly {
    }
    
    /**
     * Allows access to users who can manage courses
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER')")
    public @interface CourseManagement {
    }
    
    /**
     * Allows access to users who can view analytics
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'ANALYST', 'TEACHER')")
    public @interface AnalyticsAccess {
    }
    
    /**
     * Allows access to authenticated users only (any role except GUEST)
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER', 'ANALYST', 'MODERATOR', 'STUDENT')")
    public @interface AuthenticatedUsers {
    }
    
    /**
     * Allows access to users who can moderate content
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MODERATOR')")
    public @interface ContentModeration {
    }
}
