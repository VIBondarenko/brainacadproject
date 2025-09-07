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
import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import io.github.vibondarenko.clavionx.service.TwoFactorService;

import jakarta.validation.Valid;

/**
 * Controller for managing Two-Factor Authentication settings
 */
@Controller
@RequestMapping("/settings")
public class TwoFactorSettingsController {

    private final TwoFactorService twoFactorService;
    private final UserRepository userRepository;

    public TwoFactorSettingsController(TwoFactorService twoFactorService, UserRepository userRepository) {
        this.twoFactorService = twoFactorService;
        this.userRepository = userRepository;
    }

    /**
     * Show 2FA settings page
     */
    @GetMapping("/2fa")
    public String showTwoFactorSettings(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return "redirect:/profile";
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
        model.addAttribute("pageTitle", "Two-Factor Authentication Settings");
        model.addAttribute("pageDescription", "Manage your account security");
        model.addAttribute("pageIcon", "fa-shield-alt");

        return "settings/2fa";
    }

    /**
     * Enable 2FA - first step (choose method and send test code)
     */
    @PostMapping("/2fa/enable")
    public String enableTwoFactor(@Valid @ModelAttribute("settingsDto") TwoFactorSettingsDto settingsDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/settings/2fa";
        }

        User user = userOptional.get();

        // Validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("twoFactorMethods", TwoFactorMethod.values());
            model.addAttribute("pageTitle", "Two-Factor Authentication Settings");
            model.addAttribute("pageDescription", "Manage your account security");
            model.addAttribute("pageIcon", "fa-shield-alt");
            return "settings/2fa";
        }

        // Validate method availability
        TwoFactorMethod selectedMethod = settingsDto.getPreferredMethod();
        if (selectedMethod == TwoFactorMethod.PHONE && !settingsDto.canUsePhone()) {
            redirectAttributes.addFlashAttribute("error", "Phone number is required for SMS verification");
            return "redirect:/settings/2fa";
        }

        if (selectedMethod == TwoFactorMethod.BOTH && !settingsDto.canUseBoth()) {
            redirectAttributes.addFlashAttribute("error", "Both email and phone number are required for dual verification");
            return "redirect:/settings/2fa";
        }

        // Send test verification code
        boolean codeSent = twoFactorService.generateAndSendCode(user, selectedMethod);
        if (!codeSent) {
            redirectAttributes.addFlashAttribute("error", "Failed to send verification code. Please try again.");
            return "redirect:/settings/2fa";
        }

        // Store method in session for verification step
        model.addAttribute("verificationDto", new TwoFactorVerificationDto());
        model.addAttribute("selectedMethod", selectedMethod);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Verify Two-Factor Authentication");
        model.addAttribute("pageDescription", "Enter the verification code to enable 2FA");
        model.addAttribute("pageIcon", "fa-key");

        redirectAttributes.addFlashAttribute("success", "Verification code sent! Please check your " + 
            (selectedMethod == TwoFactorMethod.EMAIL ? "email" : 
             selectedMethod == TwoFactorMethod.PHONE ? "phone" : "email and phone") + ".");
        redirectAttributes.addFlashAttribute("pendingMethod", selectedMethod.name());
        
        return "redirect:/settings/2fa/verify";
    }

    /**
     * Show verification page for enabling 2FA
     */
    @GetMapping("/2fa/verify")
    public String showVerificationPage(Model model, Authentication authentication,
                                     @RequestParam(value = "method", required = false) String methodParam) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            return "redirect:/settings/2fa";
        }

        User user = userOptional.get();

        // Get method from flash attributes or parameter
        String pendingMethod = (String) model.asMap().get("pendingMethod");
        if (pendingMethod == null && methodParam != null) {
            pendingMethod = methodParam;
        }

        if (pendingMethod == null) {
            return "redirect:/settings/2fa";
        }

        TwoFactorMethod method;
        try {
            method = TwoFactorMethod.valueOf(pendingMethod);
        } catch (IllegalArgumentException e) {
            return "redirect:/settings/2fa";
        }

        model.addAttribute("verificationDto", new TwoFactorVerificationDto());
        model.addAttribute("selectedMethod", method);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Verify Two-Factor Authentication");
        model.addAttribute("pageDescription", "Enter the verification code to enable 2FA");
        model.addAttribute("pageIcon", "fa-key");

        return "settings/2fa-verify";
    }

    /**
     * Verify code and complete 2FA setup
     */
    @PostMapping("/2fa/verify")
    public String verifyAndEnableTwoFactor(@Valid @ModelAttribute("verificationDto") TwoFactorVerificationDto verificationDto,
                                          BindingResult bindingResult,
                                          @RequestParam("method") String methodParam,
                                          Model model,
                                          Authentication authentication,
                                          RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/settings/2fa";
        }

        User user = userOptional.get();
        
        TwoFactorMethod method;
        try {
            method = TwoFactorMethod.valueOf(methodParam);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid method specified");
            return "redirect:/settings/2fa";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("selectedMethod", method);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Verify Two-Factor Authentication");
            model.addAttribute("pageDescription", "Enter the verification code to enable 2FA");
            model.addAttribute("pageIcon", "fa-key");
            return "settings/2fa-verify";
        }

        // Verify the code
        boolean verified = twoFactorService.verifyCode(user, verificationDto.getVerificationCode());
        if (!verified) {
            model.addAttribute("error", "Invalid verification code. Please try again.");
            model.addAttribute("selectedMethod", method);
            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Verify Two-Factor Authentication");
            model.addAttribute("pageDescription", "Enter the verification code to enable 2FA");
            model.addAttribute("pageIcon", "fa-key");
            return "settings/2fa-verify";
        }

        // Enable 2FA
        boolean enabled = twoFactorService.enableTwoFactor(user, method);
        if (!enabled) {
            redirectAttributes.addFlashAttribute("error", "Failed to enable two-factor authentication");
            return "redirect:/settings/2fa";
        }

        redirectAttributes.addFlashAttribute("success", 
            "Two-factor authentication has been successfully enabled with " + method.getDisplayName() + "!");
        return "redirect:/settings/2fa";
    }

    /**
     * Disable 2FA
     */
    @PostMapping("/2fa/disable")
    public String disableTwoFactor(Authentication authentication, RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/settings/2fa";
        }

        User user = userOptional.get();

        boolean disabled = twoFactorService.disableTwoFactor(user);
        if (!disabled) {
            redirectAttributes.addFlashAttribute("error", "Failed to disable two-factor authentication");
        } else {
            redirectAttributes.addFlashAttribute("success", "Two-factor authentication has been disabled");
        }

        return "redirect:/settings/2fa";
    }

    /**
     * Send test code (for already enabled 2FA)
     */
    @PostMapping("/2fa/test")
    public String sendTestCode(Authentication authentication, RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User not found");
            return "redirect:/settings/2fa";
        }

        User user = userOptional.get();

        if (!user.isTwoFactorEnabled() || user.getPreferredTwoFactorMethod() == null) {
            redirectAttributes.addFlashAttribute("error", "Two-factor authentication is not enabled");
            return "redirect:/settings/2fa";
        }

        boolean codeSent = twoFactorService.generateAndSendCode(user, user.getPreferredTwoFactorMethod());
        if (!codeSent) {
            redirectAttributes.addFlashAttribute("error", "Failed to send test code");
        } else {
            redirectAttributes.addFlashAttribute("success", "Test code sent successfully!");
        }

        return "redirect:/settings/2fa";
    }
}



