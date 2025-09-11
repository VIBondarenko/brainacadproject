package io.github.vibondarenko.clavionx.security.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Composed security annotations for common access policies.
 */
public final class SecurityAnnotations {
    private SecurityAnnotations() {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasAnyRole(T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ADMIN.name())")
    public @interface AdminOnly {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasAnyRole("
        + "T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).MANAGER.name())")
    public @interface AdminOrManager {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasAnyRole("
        + "T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).MANAGER.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ANALYST.name())")
    public @interface AnalyticsAccess {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasAnyRole("
        + "T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).MANAGER.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).TEACHER.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ANALYST.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).MODERATOR.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).STUDENT.name())")
    public @interface DashboardAccess {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasAnyRole("
        + "T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).MANAGER.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).TEACHER.name())")
    public @interface CourseManage {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasAnyRole("
        + "T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).ADMIN.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).MANAGER.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).TEACHER.name(), "
        + "T(io.github.vibondarenko.clavionx.security.Role).STUDENT.name())")
    public @interface StudentArea {}

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @PreAuthorize("hasRole(T(io.github.vibondarenko.clavionx.security.Role).SUPER_ADMIN.name())")
    public @interface SuperAdminOnly {}
}
