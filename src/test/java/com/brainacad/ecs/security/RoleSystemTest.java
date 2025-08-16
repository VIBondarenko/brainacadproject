package com.brainacad.ecs.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class demonstrating the new role and permission system
 */
class RoleSystemTest {

    @Test
    @DisplayName("Should verify SUPER_ADMIN has all administrative permissions")
    void superAdminShouldHaveAllPermissions() {
        // Given
        Role superAdmin = Role.SUPER_ADMIN;
        
        // When
        Set<Permission> permissions = superAdmin.getPermissions();
        Collection<String> authorities = superAdmin.getAuthorities();
        
        // Then
        assertThat(permissions).contains(
            Permission.SYSTEM_MANAGE,
            Permission.USER_MANAGE_ALL,
            Permission.COURSE_MANAGE_ALL,
            Permission.ANALYTICS_VIEW_ALL
        );
        
        assertThat(authorities).contains(
            "ROLE_SUPER_ADMIN",
            "PERMISSION_SYSTEM_MANAGE",
            "PERMISSION_USER_MANAGE_ALL"
        );
    }

    @Test
    @DisplayName("Should verify TEACHER has appropriate course permissions")
    void teacherShouldHaveOwnCoursePermissions() {
        // Given
        Role teacher = Role.TEACHER;
        
        // When
        Set<Permission> permissions = teacher.getPermissions();
        
        // Then
        assertThat(permissions).contains(
            Permission.COURSE_CREATE_OWN,
            Permission.COURSE_EDIT_OWN,
            Permission.STUDENT_MANAGE_OWN,
            Permission.TASK_MANAGE_OWN,
            Permission.GRADE_MANAGE_OWN
        );
        
        // Should NOT have all-course permissions
        assertThat(permissions).doesNotContain(
            Permission.COURSE_MANAGE_ALL,
            Permission.STUDENT_MANAGE_ALL,
            Permission.SYSTEM_MANAGE
        );
    }

    @Test
    @DisplayName("Should verify STUDENT has limited permissions")
    void studentShouldHaveLimitedPermissions() {
        // Given
        Role student = Role.STUDENT;
        
        // When
        Set<Permission> permissions = student.getPermissions();
        
        // Then
        assertThat(permissions).contains(
            Permission.COURSE_VIEW,
            Permission.COURSE_ENROLL,
            Permission.TASK_SUBMIT,
            Permission.GRADE_VIEW_OWN,
            Permission.PROGRESS_VIEW_OWN
        );
        
        // Should NOT have management permissions
        assertThat(permissions).doesNotContain(
            Permission.COURSE_CREATE,
            Permission.STUDENT_MANAGE_ALL,
            Permission.ANALYTICS_VIEW_ALL
        );
    }

    @Test
    @DisplayName("Should verify role hierarchy")
    void shouldVerifyRoleHierarchy() {
        // Given & When & Then
        assertThat(Role.SUPER_ADMIN.hasHigherOrEqualPriorityThan(Role.ADMIN)).isTrue();
        assertThat(Role.ADMIN.hasHigherOrEqualPriorityThan(Role.MANAGER)).isTrue();
        assertThat(Role.TEACHER.hasHigherOrEqualPriorityThan(Role.STUDENT)).isTrue();
        assertThat(Role.STUDENT.hasHigherOrEqualPriorityThan(Role.SUPER_ADMIN)).isFalse();
    }

    @Test
    @DisplayName("Should verify role groups")
    void shouldVerifyRoleGroups() {
        // Given & When
        Set<Role> adminRoles = Role.getAdministrativeRoles();
        Set<Role> educationalRoles = Role.getEducationalRoles();
        Set<Role> courseManagementRoles = Role.getCourseManagementRoles();
        
        // Then
        assertThat(adminRoles).contains(Role.SUPER_ADMIN, Role.ADMIN, Role.MANAGER);
        assertThat(educationalRoles).contains(Role.ADMIN, Role.MANAGER, Role.TEACHER, Role.STUDENT);
        assertThat(courseManagementRoles).contains(Role.SUPER_ADMIN, Role.ADMIN, Role.MANAGER, Role.TEACHER);
    }

    @Test
    @DisplayName("Should verify permission categorization")
    void shouldVerifyPermissionCategorization() {
        // Given & When & Then
        assertThat(Permission.SYSTEM_MANAGE.isAdministrative()).isTrue();
        assertThat(Permission.USER_MANAGE_ALL.isAdministrative()).isTrue();
        assertThat(Permission.COURSE_VIEW.isEducational()).isTrue();
        assertThat(Permission.STUDENT_MANAGE_OWN.isEducational()).isTrue();
        assertThat(Permission.PROFILE_EDIT_OWN.isAdministrative()).isFalse();
    }

    @Test
    @DisplayName("Should generate correct Spring Security authorities")
    void shouldGenerateCorrectAuthorities() {
        // Given
        Role admin = Role.ADMIN;
        
        // When
        Collection<? extends GrantedAuthority> authorities = SecurityUtils.getAuthorities(admin);
        
        // Then
        assertThat(authorities).extracting(GrantedAuthority::getAuthority)
            .contains(
                "ROLE_ADMIN",
                "PERMISSION_COURSE_MANAGE_ALL",
                "PERMISSION_STUDENT_MANAGE_ALL",
                "PERMISSION_ANALYTICS_VIEW_ALL"
            );
    }

    @Test
    @DisplayName("Should create proper Spring expressions")
    void shouldCreateProperSpringExpressions() {
        // Given & When
        String roleExpr = SecurityUtils.roleExpression(Role.TEACHER);
        String permissionExpr = SecurityUtils.permissionExpression(Permission.COURSE_CREATE);
        String multiRoleExpr = SecurityUtils.anyRoleExpression(Role.ADMIN, Role.MANAGER);
        
        // Then
        assertThat(roleExpr).isEqualTo("hasRole('TEACHER')");
        assertThat(permissionExpr).isEqualTo("hasAuthority('PERMISSION_COURSE_CREATE')");
        assertThat(multiRoleExpr).isEqualTo("hasAnyRole('ADMIN', 'MANAGER')");
    }
    
    @Test
    @DisplayName("Should verify role display names")
    void shouldVerifyRoleDisplayNames() {
        // Given & When & Then
        assertThat(Role.SUPER_ADMIN.getDisplayName()).isEqualTo("Super Administrator");
        assertThat(Role.TEACHER.getDisplayName()).isEqualTo("Teacher");
        assertThat(Role.STUDENT.getDisplayName()).isEqualTo("Student");
    }
    
    @Test
    @DisplayName("Should verify permission descriptions")
    void shouldVerifyPermissionDescriptions() {
        // Given & When & Then
        assertThat(Permission.SYSTEM_MANAGE.getDescription())
            .isEqualTo("Manage system settings, configuration, and infrastructure");
        assertThat(Permission.COURSE_CREATE.getDescription())
            .isEqualTo("Create new courses");
    }
}
