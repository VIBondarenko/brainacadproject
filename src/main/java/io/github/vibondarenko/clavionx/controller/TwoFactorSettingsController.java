package io.github.vibondarenko.clavionx.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.vibondarenko.clavionx.dto.TwoFactorSettingsDto;
import io.github.vibondarenko.clavionx.dto.TwoFactorVerificationDto;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import io.github.vibondarenko.clavionx.service.TwoFactorService;
import io.github.vibondarenko.clavionx.view.ViewAttributes;
import jakarta.validation.Valid;

/**
 * Controller for managing Two-Factor Authentication settings
 */
@Controller
@RequestMapping("/settings")
public class TwoFactorSettingsController {

    private final TwoFactorService twoFactorService;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection
     * @param twoFactorService Service for handling 2FA operations
     * @param userRepository Repository for accessing user data
     */
    public TwoFactorSettingsController(TwoFactorService twoFactorService, UserRepository userRepository) {
        this.twoFactorService = twoFactorService;
        this.userRepository = userRepository;
    }

    /**
     * Show 2FA settings page
     * @param model Model to hold attributes for the view
     * @param authentication Current user authentication
     * @return View name for 2FA settings page or redirect to login if not authenticated
     */
    @GetMapping("/2fa")
    public String showTwoFactorSettings(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Paths.REDIRECT_LOGIN;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return Paths.REDIRECT_PROFILE;
        }

        User user = userOptional.get();
        
        // Create settings DTO
        TwoFactorSettingsDto settingsDto = new TwoFactorSettingsDto(
            user.isTwoFactorEnabled(),
            user.getPreferredTwoFactorMethod(),
            user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty(),
            user.getEmail() != null && !user.getEmail().trim().isEmpty()
        );

        // Add model attributes
        model.addAttribute("settingsDto", settingsDto);
        model.addAttribute("user", user);
        model.addAttribute("twoFactorMethods", TwoFactorMethod.values());
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Two-Factor Authentication Settings");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Manage your account security");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-shield-alt");

        return "settings/2fa";
    }

    /**
     * Enable 2FA - first step (choose method and send test code)
     * @param settingsDto DTO containing the selected 2FA settings
     * @param bindingResult Result of validation
     * @param model Model to hold attributes for the view
     * @param authentication Current user authentication
     * @param redirectAttributes Attributes for redirect scenarios
     * @return Redirect to verification page or back to settings with errors
     */
    @PostMapping("/2fa/enable")
    public String enableTwoFactor(@Valid @ModelAttribute("settingsDto") TwoFactorSettingsDto settingsDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Paths.REDIRECT_LOGIN;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        User user = userOptional.get();

        // Validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("twoFactorMethods", TwoFactorMethod.values());
            model.addAttribute(ViewAttributes.PAGE_TITLE, "Two-Factor Authentication Settings");
            model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Manage your account security");
            model.addAttribute(ViewAttributes.PAGE_ICON, "fa-shield-alt");
            return "settings/2fa";
        }

        // Validate method availability
        TwoFactorMethod selectedMethod = settingsDto.getPreferredMethod();
        if (selectedMethod == TwoFactorMethod.PHONE && !settingsDto.canUsePhone()) {
            redirectAttributes.addFlashAttribute("error", "Phone number is required for SMS verification");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        if (selectedMethod == TwoFactorMethod.BOTH && !settingsDto.canUseBoth()) {
            redirectAttributes.addFlashAttribute("error", "Both email and phone number are required for dual verification");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        // Send test verification code
        boolean codeSent = twoFactorService.generateAndSendCode(user, selectedMethod);
        if (!codeSent) {
            redirectAttributes.addFlashAttribute("error", "Failed to send verification code. Please try again.");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        // Store method in session for verification step
        model.addAttribute("verificationDto", new TwoFactorVerificationDto());
        model.addAttribute("selectedMethod", selectedMethod);
        model.addAttribute("user", user);
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Verify Two-Factor Authentication");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Enter the verification code to enable 2FA");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-key");

        String contactType = switch (selectedMethod) {
            case EMAIL -> "email";
            case PHONE -> "phone";
            case BOTH -> "email and phone";
        };
        redirectAttributes.addFlashAttribute("success", "Verification code sent! Please check your " + contactType + ".");
        redirectAttributes.addFlashAttribute("pendingMethod", selectedMethod.name());

        return Paths.REDIRECT_SETTINGS_2FA_VERIFY;
    }

    /**
     * Show verification page for enabling 2FA
     * @param model Model to hold attributes for the view
     * @param authentication Current user authentication
     * @param methodParam Optional method parameter to indicate which method is being verified
     * @return View name for verification page or redirect to settings if no method is pending
     */
    @GetMapping("/2fa/verify")
    public String showVerificationPage(Model model, Authentication authentication,
                                        @RequestParam(value = "method", required = false) String methodParam) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Paths.REDIRECT_LOGIN;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        User user = userOptional.get();

        // Get method from flash attributes or parameter
        String pendingMethod = (String) model.asMap().get("pendingMethod");
        if (pendingMethod == null && methodParam != null) {
            pendingMethod = methodParam;
        }

        if (pendingMethod == null) {
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        TwoFactorMethod method;
        try {
            method = TwoFactorMethod.valueOf(pendingMethod);
        } catch (IllegalArgumentException e) {
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        model.addAttribute("verificationDto", new TwoFactorVerificationDto());
        model.addAttribute("selectedMethod", method);
        model.addAttribute("user", user);
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Verify Two-Factor Authentication");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Enter the verification code to enable 2FA");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-key");

        return Paths.SETTINGS_2FAVERIFY;
    }

    /**
     * Verify code and complete 2FA setup
     * @param verificationDto DTO containing the verification code
     * @param bindingResult Result of validation
     * @param methodParam Method being verified
     * @param model Model to hold attributes for the view
     * @param authentication Current user authentication
     * @param redirectAttributes Attributes for redirect scenarios
     * @return Redirect to settings with success or error messages
     */
    @PostMapping("/2fa/verify")
    public String verifyAndEnableTwoFactor(@Valid @ModelAttribute("verificationDto") TwoFactorVerificationDto verificationDto,
                                            BindingResult bindingResult,
                                            @RequestParam("method") String methodParam,
                                            Model model,
                                            Authentication authentication,
                                            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Paths.REDIRECT_LOGIN;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        User user = userOptional.get();
        
        TwoFactorMethod method;
        try {
            method = TwoFactorMethod.valueOf(methodParam);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid method specified");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("selectedMethod", method);
            model.addAttribute("user", user);
            model.addAttribute(ViewAttributes.PAGE_TITLE, "Verify Two-Factor Authentication");
            model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Enter the verification code to enable 2FA");
            model.addAttribute(ViewAttributes.PAGE_ICON, "fa-key");
            return Paths.SETTINGS_2FAVERIFY;
        }

        // Verify the code
        boolean verified = twoFactorService.verifyCode(user, verificationDto.getVerificationCode());
        if (!verified) {
            model.addAttribute("error", "Invalid verification code. Please try again.");
            model.addAttribute("selectedMethod", method);
            model.addAttribute("user", user);
            model.addAttribute(ViewAttributes.PAGE_TITLE, "Verify Two-Factor Authentication");
            model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Enter the verification code to enable 2FA");
            model.addAttribute(ViewAttributes.PAGE_ICON, "fa-key");
            return Paths.SETTINGS_2FAVERIFY;
        }

        // Enable 2FA
        boolean enabled = twoFactorService.enableTwoFactor(user, method);
        if (!enabled) {
            redirectAttributes.addFlashAttribute("error", "Failed to enable two-factor authentication");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        redirectAttributes.addFlashAttribute("success", 
            "Two-factor authentication has been successfully enabled with " + method.getDisplayName() + "!");
        return Paths.REDIRECT_SETTINGS_2FA;
    }

    /**
     * Disable 2FA
     * @param authentication Current user authentication
     * @param redirectAttributes Attributes for redirect scenarios
     * @return Redirect to settings with success or error messages
     */
    @PostMapping("/2fa/disable")
    public String disableTwoFactor(Authentication authentication, RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Paths.REDIRECT_LOGIN;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        User user = userOptional.get();

        boolean disabled = twoFactorService.disableTwoFactor(user);
        if (!disabled) {
            redirectAttributes.addFlashAttribute("error", "Failed to disable two-factor authentication");
        } else {
            redirectAttributes.addFlashAttribute("success", "Two-factor authentication has been disabled");
        }

        return Paths.REDIRECT_SETTINGS_2FA;
    }

    /**
     * Send test code (for already enabled 2FA)
     * @param authentication Current user authentication
     * @param redirectAttributes Attributes for redirect scenarios
     * @return Redirect to settings with success or error messages
     */
    @PostMapping("/2fa/test")
    public String sendTestCode(Authentication authentication, RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Paths.REDIRECT_LOGIN;
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        User user = userOptional.get();

        if (!user.isTwoFactorEnabled() || user.getPreferredTwoFactorMethod() == null) {
            redirectAttributes.addFlashAttribute("error", "Two-factor authentication is not enabled");
            return Paths.REDIRECT_SETTINGS_2FA;
        }

        boolean codeSent = twoFactorService.generateAndSendCode(user, user.getPreferredTwoFactorMethod());
        if (!codeSent) {
            redirectAttributes.addFlashAttribute("error", "Failed to send test code");
        } else {
            redirectAttributes.addFlashAttribute("success", "Test code sent successfully!");
        }

        return Paths.REDIRECT_SETTINGS_2FA;
    }
}