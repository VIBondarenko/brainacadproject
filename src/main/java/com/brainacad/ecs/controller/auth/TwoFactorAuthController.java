package com.brainacad.ecs.controller.auth;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;
import com.brainacad.ecs.security.TwoFactorAuthenticationProvider;
import com.brainacad.ecs.security.TwoFactorAuthenticationToken;
import com.brainacad.ecs.service.TrustedDeviceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/**
 * Controller for handling Two-Factor Authentication
 */
@Controller
@RequestMapping("/auth")
public class TwoFactorAuthController {

    private final TwoFactorAuthenticationProvider twoFactorAuthenticationProvider;
    private final UserRepository userRepository;
    private final TrustedDeviceService trustedDeviceService;

    public TwoFactorAuthController(TwoFactorAuthenticationProvider twoFactorAuthenticationProvider, 
                                    UserRepository userRepository,
                                    TrustedDeviceService trustedDeviceService) {
        this.twoFactorAuthenticationProvider = twoFactorAuthenticationProvider;
        this.userRepository = userRepository;
        this.trustedDeviceService = trustedDeviceService;
    }

    /**
     * Shows the 2FA verification page
     */
    @GetMapping("/2fa")
    public String show2FAPage(Model model, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (!(auth instanceof TwoFactorAuthenticationToken)) {
            return "redirect:/login";
        }

        TwoFactorAuthenticationToken twoFactorAuth = (TwoFactorAuthenticationToken) auth;
        String username = twoFactorAuth.getUsername();
        
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(username);
        if (userOpt.isEmpty()) {
            return "redirect:/login?error=true";
        }

        User user = userOpt.get();
        addPageAttributes(model, user);

        return "auth/2fa";
    }

    /**
     * Handles 2FA code verification
     */
    @PostMapping("/2fa/verify")
    public String verifyTwoFactorCode(
            @RequestParam String code,
            @RequestParam(required = false) String rememberDevice,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            
            if (!(currentAuth instanceof TwoFactorAuthenticationToken)) {
                return "redirect:/login?error=true";
            }

            TwoFactorAuthenticationToken twoFactorToken = (TwoFactorAuthenticationToken) currentAuth;
            
            // Use provider to verify 2FA code
            Authentication fullyAuthenticated = twoFactorAuthenticationProvider.verifyTwoFactorCode(
                twoFactorToken.getUsername(), code);
            
            if (fullyAuthenticated == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid verification code. Please try again.");
                return "redirect:/auth/2fa?error=true";
            }

            // Update security context
            SecurityContextHolder.getContext().setAuthentication(fullyAuthenticated);
            
            // Store in session
            request.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

            // Handle "Remember this device" functionality
            if (REMEMBER_DEVICE_CHECKED.equals(rememberDevice)) {
                String username = twoFactorToken.getUsername();
                Optional<User> userOpt = userRepository.findByUsernameOrEmail(username);
                if (userOpt.isPresent()) {
                    trustedDeviceService.trustDevice(userOpt.get(), request);
                }
            }

            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred during verification. Please try again.");
            return "redirect:/auth/2fa?error=true";
        }
    }

    private void addPageAttributes(Model model, User user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userEmail", user.getEmail());
        model.addAttribute("preferredMethod", user.getPreferredTwoFactorMethod().toString().toLowerCase());
    }

    private static final String REMEMBER_DEVICE_CHECKED = "on";
}
