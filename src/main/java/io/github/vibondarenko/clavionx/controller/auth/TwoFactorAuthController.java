package io.github.vibondarenko.clavionx.controller.auth;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.security.TwoFactorAuthenticationProvider;
import io.github.vibondarenko.clavionx.security.TwoFactorAuthenticationToken;
import io.github.vibondarenko.clavionx.service.TrustedDeviceService;
import jakarta.servlet.http.HttpServletRequest;

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
            return Paths.REDIRECT_LOGIN;
        }

        TwoFactorAuthenticationToken twoFactorAuth = (TwoFactorAuthenticationToken) auth;
        String username = twoFactorAuth.getUsername();
        
        Optional<User> userOpt = userRepository.findByUsernameOrEmail(username);
        if (userOpt.isEmpty()) {
            return Paths.REDIRECT_LOGIN_ERROR;
        }

        User user = userOpt.get();
        addPageAttributes(model, user);

        return Paths.AUTH_2FA;
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
                return Paths.REDIRECT_LOGIN_ERROR;
            }

            TwoFactorAuthenticationToken twoFactorToken = (TwoFactorAuthenticationToken) currentAuth;
            
            // Use provider to verify 2FA code
            Authentication fullyAuthenticated = twoFactorAuthenticationProvider.verifyTwoFactorCode(
                twoFactorToken.getUsername(), code);
            
            if (fullyAuthenticated == null) {
                redirectAttributes.addFlashAttribute("error", "Invalid verification code. Please try again.");
                return Paths.REDIRECT_AUTH_2FA_ERROR;
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

            return  Paths.REDIRECT_DASHBOARD;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred during verification. Please try again.");
            return Paths.REDIRECT_AUTH_2FA_ERROR;
        }
    }

    private void addPageAttributes(Model model, User user) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("userEmail", user.getEmail());
        model.addAttribute("preferredMethod", user.getPreferredTwoFactorMethod().toString().toLowerCase());
    }

    private static final String REMEMBER_DEVICE_CHECKED = "on";
}



