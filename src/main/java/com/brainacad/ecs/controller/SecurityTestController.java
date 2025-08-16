package com.brainacad.ecs.controller;

import com.brainacad.ecs.security.Role;
import com.brainacad.ecs.security.SecurityUtils;
import com.brainacad.ecs.security.annotations.SecurityAnnotations.*;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Security Test Controller
 * Demonstrates role-based access control functionality
 */
@Controller
@RequestMapping("/security-test")
public class SecurityTestController {

    /**
     * Public page - accessible to all authenticated users
     */
    @GetMapping("/public")
    public String publicPage(Model model, Authentication auth) {
        model.addAttribute("message", "This is a public page accessible to all authenticated users");
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        return "security-test/public";
    }

    /**
     * Admin only page
     */
    @GetMapping("/admin")
    @AdminOnly
    public String adminPage(Model model, Authentication auth) {
        model.addAttribute("message", "This is an Admin-only page");
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "ADMIN or SUPER_ADMIN");
        return "security-test/admin";
    }

    /**
     * Teacher only page
     */
    @GetMapping("/teacher")
    @TeacherOnly
    public String teacherPage(Model model, Authentication auth) {
        model.addAttribute("message", "This is a Teacher-only page");
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "TEACHER, MANAGER, ADMIN or SUPER_ADMIN");
        return "security-test/teacher";
    }

    /**
     * Course management page
     */
    @GetMapping("/course-management")
    @CourseManagement
    public String courseManagementPage(Model model, Authentication auth) {
        model.addAttribute("message", "This page requires Course Management permissions");
        model.addAttribute("username", auth.getName());
        model.addAttribute("requiredPermissions", "COURSE_CREATE, COURSE_EDIT, COURSE_DELETE");
        return "security-test/course-management";
    }

    /**
     * Analytics page
     */
    @GetMapping("/analytics")
    @AnalyticsAccess
    public String analyticsPage(Model model, Authentication auth) {
        model.addAttribute("message", "This page requires Analytics Access");
        model.addAttribute("username", auth.getName());
        model.addAttribute("requiredRoles", "SUPER_ADMIN, ADMIN, ANALYST");
        return "security-test/analytics";
    }

    /**
     * Role information page - shows current user's role and permissions
     */
    @GetMapping("/my-role")
    public String myRolePage(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("authorities", auth.getAuthorities());
        
        // Try to determine the role by checking authorities
        for (Role role : Role.values()) {
            boolean hasRole = auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role.name()));
            if (hasRole) {
                model.addAttribute("currentRole", role.name());
                model.addAttribute("rolePermissions", role.getPermissions());
                break;
            }
        }
        
        return "security-test/my-role";
    }
}
