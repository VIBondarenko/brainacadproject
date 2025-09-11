package io.github.vibondarenko.clavionx.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

import io.github.vibondarenko.clavionx.dto.PasswordChangeDto;
import io.github.vibondarenko.clavionx.dto.UserProfileDto;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import io.github.vibondarenko.clavionx.service.ProfileService;
import io.github.vibondarenko.clavionx.view.ViewAttributes;
import jakarta.validation.Valid;

/**
 * Controller for user profile management
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final ProfileService profileService;
    public ProfileController(UserRepository userRepository, ProfileService profileService) {
        this.userRepository = userRepository;
        this.profileService = profileService;
    }

    /**
     * Display user profile page
     */
    @GetMapping
    public String profile(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            return "redirect:/login";
        }
        User user = userOptional.get();
        addBasicProfileAttributes(model, user, authentication);
        addAvatarAndDates(model, user);
        addRoleAndAccountStatus(model, user);
        addPermissions(model, authentication);
        addTwoFactorStatus(model, user);
        model.addAttribute("totalSessions", generateMockSessions(user));
        return "profile/index";
    }

    /**
     * Add basic user profile attributes to model
     */
    private void addBasicProfileAttributes(Model model, User user, Authentication authentication) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "User Profile");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "View and edit your profile details");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user");

        model.addAttribute("username", user.getUsername());
        model.addAttribute("authorities", authentication.getAuthorities());
        model.addAttribute("userEmail", user.getEmail());
        model.addAttribute("fullName", user.getName() + " " + user.getLastName());
        model.addAttribute("firstName", user.getName());
        model.addAttribute("lastName", user.getLastName());
        model.addAttribute("age", user.getAge());
    }

    /**
     * Add avatar path and formatted dates to model
     */
    private void addAvatarAndDates(Model model, User user) {
        String avatarPath = user.getAvatarPath();
        model.addAttribute("avatarPath", (avatarPath != null && !avatarPath.isEmpty()) ? avatarPath : "/images/default-avatar.svg");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        model.addAttribute("joinDate", user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "Unknown");
        model.addAttribute("lastLogin", user.getUpdatedAt() != null ? user.getUpdatedAt().format(dateTimeFormatter) : "Never");
    }

    /**
     * Add role and account status attributes to model
     */
    private void addRoleAndAccountStatus(Model model, User user) {
        String roleDisplayName = getRoleDisplayName("ROLE_" + user.getRole().name());
        model.addAttribute("activeRole", roleDisplayName);
        model.addAttribute("userRole", user.getRole().name());
        model.addAttribute("accountEnabled", user.isEnabled());
        model.addAttribute("accountNonExpired", user.isAccountNonExpired());
        model.addAttribute("accountNonLocked", user.isAccountNonLocked());
        model.addAttribute("credentialsNonExpired", user.isCredentialsNonExpired());
    }

    /**
     * Add permissions and specific permission checks to model
     */
    private void addPermissions(Model model, Authentication authentication) {
        String permissionsList = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(", "));
        model.addAttribute("permissionsList", permissionsList);

        model.addAttribute("hasSystemManage", hasPermission(authentication, "PERMISSION_SYSTEM_MANAGE"));
        model.addAttribute("hasUserManage", hasPermission(authentication, "PERMISSION_USER_MANAGE"));
        model.addAttribute("hasCourseManage", hasPermission(authentication, "PERMISSION_COURSE_MANAGE"));
        model.addAttribute("hasAnalytics", hasPermission(authentication, "PERMISSION_ANALYTICS"));
        model.addAttribute("hasAuditView", hasPermission(authentication, "PERMISSION_AUDIT_VIEW"));
        model.addAttribute("hasBackupManage", hasPermission(authentication, "PERMISSION_BACKUP_MANAGE"));
    }

    /**
     * Add two-factor authentication status to model
     */
    private void addTwoFactorStatus(Model model, User user) {
        boolean twoFactorEnabled = user.isTwoFactorEnabled();
        model.addAttribute("twoFactorEnabled", twoFactorEnabled);
        
        if (twoFactorEnabled && user.getPreferredTwoFactorMethod() != null) {
            TwoFactorMethod method = user.getPreferredTwoFactorMethod();
            model.addAttribute("twoFactorMethod", method.name());
            model.addAttribute("twoFactorMethodDisplay", method.getDisplayName());
        } else {
            model.addAttribute("twoFactorMethod", "NONE");
            model.addAttribute("twoFactorMethodDisplay", twoFactorEnabled ? "Не настроен" : "Отключено");
        }
    }

    /**
     * Check if user has specific permission
     */
    private boolean hasPermission(Authentication authentication, String permission) {
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().contains(permission));
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
        return switch (roleName) {
            case "ROLE_SUPER_ADMIN", "SUPER_ADMIN" -> "Super Administrator";
            case "ROLE_ADMIN", "ADMIN" -> "Administrator";
            case "ROLE_MANAGER", "MANAGER" -> "Manager";
            case "ROLE_TEACHER", "TEACHER" -> "Teacher";
            case "ROLE_STUDENT", "STUDENT" -> "Student";
            case "ROLE_ANALYST", "ANALYST" -> "Analyst";
            default -> "User";
        };
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
                model.addAttribute(ViewAttributes.PAGE_TITLE, "Edit Profile");
                model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Edit your personal information");
                model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user-edit");

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
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Change Password");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Change your account password");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-key");
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