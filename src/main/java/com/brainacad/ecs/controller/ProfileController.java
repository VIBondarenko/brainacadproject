package com.brainacad.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for user profile management
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Display user profile page
     */
    @GetMapping
    public String profile(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            
            // Find user in database
            Optional<User> userOptional = userRepository.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Basic user information
                model.addAttribute("username", user.getUsername());
                model.addAttribute("authorities", authentication.getAuthorities());
                
                // Real user data from database
                model.addAttribute("userEmail", user.getEmail());
                model.addAttribute("fullName", user.getName() + " " + user.getLastName());
                model.addAttribute("firstName", user.getName());
                model.addAttribute("lastName", user.getLastName());
                model.addAttribute("age", user.getAge());
                
                // Format dates properly
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                
                if (user.getCreatedAt() != null) {
                    model.addAttribute("joinDate", user.getCreatedAt().format(formatter));
                } else {
                    model.addAttribute("joinDate", "Unknown");
                }
                
                if (user.getUpdatedAt() != null) {
                    model.addAttribute("lastLogin", user.getUpdatedAt().format(dateTimeFormatter));
                } else {
                    model.addAttribute("lastLogin", "Never");
                }
                
                // User role and permissions
                String roleDisplayName = getRoleDisplayName("ROLE_" + user.getRole().name());
                model.addAttribute("activeRole", roleDisplayName);
                model.addAttribute("userRole", user.getRole().name());
                
                // Account status
                model.addAttribute("accountEnabled", user.isEnabled());
                model.addAttribute("accountNonExpired", user.isAccountNonExpired());
                model.addAttribute("accountNonLocked", user.isAccountNonLocked());
                model.addAttribute("credentialsNonExpired", user.isCredentialsNonExpired());
                
                // Permissions list for display
                String permissionsList = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
                model.addAttribute("permissionsList", permissionsList);
                
                // Specific permission checks for UI
                boolean hasSystemManage = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("PERMISSION_SYSTEM_MANAGE"));
                boolean hasUserManage = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("PERMISSION_USER_MANAGE"));
                boolean hasCourseManage = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("PERMISSION_COURSE_MANAGE"));
                boolean hasAnalytics = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("PERMISSION_ANALYTICS"));
                boolean hasAuditView = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("PERMISSION_AUDIT_VIEW"));
                boolean hasBackupManage = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().contains("PERMISSION_BACKUP_MANAGE"));
                
                model.addAttribute("hasSystemManage", hasSystemManage);
                model.addAttribute("hasUserManage", hasUserManage);
                model.addAttribute("hasCourseManage", hasCourseManage);
                model.addAttribute("hasAnalytics", hasAnalytics);
                model.addAttribute("hasAuditView", hasAuditView);
                model.addAttribute("hasBackupManage", hasBackupManage);
                
                // Mock session data (could be implemented with session tracking)
                model.addAttribute("totalSessions", generateMockSessions(user));
                
            } else {
                // User not found in database, redirect to login
                return "redirect:/login";
            }
            
            return "profile/index";
        } else {
            return "redirect:/login";
        }
    }
    
    /**
     * Generate mock session count based on user data
     */
    private int generateMockSessions(User user) {
        // Simple mock calculation based on user ID and creation time
        if (user.getId() != null && user.getCreatedAt() != null) {
            return (int) (user.getId() * 10 + user.getCreatedAt().getDayOfYear() % 50);
        }
        return 1;
    }
    
    /**
     * Get user-friendly role display name
     */
    private String getRoleDisplayName(String roleName) {
        switch (roleName) {
            case "ROLE_SUPER_ADMIN":
            case "SUPER_ADMIN":
                return "Super Administrator";
            case "ROLE_ADMIN":
            case "ADMIN":
                return "Administrator";
            case "ROLE_MANAGER":
            case "MANAGER":
                return "Manager";
            case "ROLE_TEACHER":
            case "TEACHER":
                return "Teacher";
            case "ROLE_STUDENT":
            case "STUDENT":
                return "Student";
            case "ROLE_ANALYST":
            case "ANALYST":
                return "Analyst";
            default:
                return "User";
        }
    }
}
