package com.brainacad.ecs.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brainacad.ecs.dto.PasswordChangeDto;
import com.brainacad.ecs.dto.UserProfileDto;
import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;
import com.brainacad.ecs.service.ProfileService;

import jakarta.validation.Valid;

/**
 * Controller for user profile management
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProfileService profileService;

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

                model.addAttribute("pageTitle", "User Profile");
                model.addAttribute("pageDescription", "View and edit your profile details");
                model.addAttribute("pageIcon", "fa-user");

                // Basic user information
                model.addAttribute("username", user.getUsername());
                model.addAttribute("authorities", authentication.getAuthorities());
                
                // Real user data from database
                model.addAttribute("userEmail", user.getEmail());
                model.addAttribute("fullName", user.getName() + " " + user.getLastName());
                model.addAttribute("firstName", user.getName());
                model.addAttribute("lastName", user.getLastName());
                model.addAttribute("age", user.getAge());
                
                // Add avatar path
                String avatarPath = user.getAvatarPath();
                if (avatarPath != null && !avatarPath.isEmpty()) {
                    model.addAttribute("avatarPath", avatarPath);
                } else {
                    model.addAttribute("avatarPath", "/images/default-avatar.svg");
                }
                
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
    
    /**
     * Show profile edit form
     */
    @GetMapping("/edit")
    public String editProfile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        try {
            Optional<UserProfileDto> profileOpt = profileService.getUserProfile(username);
            if (profileOpt.isPresent()) {
                model.addAttribute("pageTitle", "Edit Profile");
                model.addAttribute("pageDescription", "Edit your personal information");
                model.addAttribute("pageIcon", "fa-user-edit");

                model.addAttribute("userProfileDto", profileOpt.get());
                return "profile/edit";
            } else {
                model.addAttribute("error", "Profile not found");
                return "redirect:/profile";
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", "Profile not found");
            return "redirect:/profile";
        }
    }
    
    /**
     * Update user profile
     */
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute UserProfileDto userProfileDto,
                                BindingResult bindingResult,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("userProfileDto", userProfileDto);
            return "profile/edit";
        }
        
        String username = authentication.getName();
        try {
            profileService.updateUserProfile(username, userProfileDto);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userProfileDto", userProfileDto);
            return "profile/edit";
        }
    }
    
    /**
     * Show password change form
     */
    @GetMapping("/change-password")
    public String changePasswordForm(Model model) {
        model.addAttribute("pageTitle", "Change Password");
        model.addAttribute("pageDescription", "Change your account password");
        model.addAttribute("pageIcon", "fa-key");
        model.addAttribute("passwordChangeDto", new PasswordChangeDto());
        return "profile/change-password";
    }
    
    /**
     * Change user password
     */
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute PasswordChangeDto passwordChangeDto,
                                    BindingResult bindingResult,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("passwordChangeDto", passwordChangeDto);
            return "profile/change-password";
        }
        
        String username = authentication.getName();
        try {
            profileService.changePassword(username, passwordChangeDto);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/profile";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("passwordChangeDto", passwordChangeDto);
            return "profile/change-password";
        }
    }
    
    /**
     * Upload user avatar
     */
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/profile";
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            redirectAttributes.addFlashAttribute("error", "Please upload a valid image file");
            return "redirect:/profile";
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            redirectAttributes.addFlashAttribute("error", "File size must be less than 5MB");
            return "redirect:/profile";
        }

        String username = authentication.getName();
        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = username + "_" + UUID.randomUUID().toString() + fileExtension;

            // Create uploads directory if it doesn't exist
            Path uploadDir = Paths.get("src/main/resources/static/uploads/avatars");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // Resize and compress image to max 1024x1024 using Thumbnailator
            Path filePath = uploadDir.resolve(uniqueFilename);
            try (var inputStream = file.getInputStream();
                    var outputStream = Files.newOutputStream(filePath)) {
                net.coobird.thumbnailator.Thumbnails.of(inputStream)
                        .size(1024, 1024)
                        .outputQuality(0.85f)
                        .toOutputStream(outputStream);
            }

            // Save avatar path to database (web-accessible path)
            String webPath = "/uploads/avatars/" + uniqueFilename;
            profileService.updateAvatar(username, webPath);

            redirectAttributes.addFlashAttribute("success", "Avatar updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to save avatar file: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }
}
