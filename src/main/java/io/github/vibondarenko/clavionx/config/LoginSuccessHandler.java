package io.github.vibondarenko.clavionx.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.security.TwoFactorAuthenticationToken;
import io.github.vibondarenko.clavionx.security.TwoFactorMethod;
import io.github.vibondarenko.clavionx.service.SessionService;
import io.github.vibondarenko.clavionx.service.TrustedDeviceService;
import io.github.vibondarenko.clavionx.service.TwoFactorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Authentication success handler
 * Creates new user session on login and handles 2FA flow
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoginSuccessHandler.class);

    private final SessionService sessionService;
    private final TwoFactorService twoFactorService;
    private final TrustedDeviceService trustedDeviceService;

    public LoginSuccessHandler(SessionService sessionService, TwoFactorService twoFactorService,
                                TrustedDeviceService trustedDeviceService) {
        this.sessionService = sessionService;
        this.twoFactorService = twoFactorService;
        this.trustedDeviceService = trustedDeviceService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        // Get user from authentication
    Object principal = authentication.getPrincipal();
    logger.info("Authentication principal class: {}", principal.getClass().getName());
        
        if (principal instanceof User user) {
            
            logger.info("User ID: {}", user.getId());
            logger.info("User class: {}", user.getClass().getName());
            logger.info("User name: {}", user.getName());
            logger.info("User username: {}", user.getUsername());
            
            // Check if user has 2FA enabled and this is a fresh login (not remember-me)
            boolean isRememberMeAuth = authentication instanceof RememberMeAuthenticationToken;
            logger.info("Is RememberMe authentication: {}", isRememberMeAuth);
            
            if (user.isTwoFactorEnabled() && !isRememberMeAuth) {
                logger.info("User has 2FA enabled, checking trusted devices");
                
                // Check if device is trusted
                boolean isDeviceTrusted = trustedDeviceService.isDeviceTrusted(user, request);
                logger.info("Is device trusted: {}", isDeviceTrusted);
                
                if (!isDeviceTrusted) {
                    // Device not trusted, require 2FA verification
                    logger.info("Device not trusted, redirecting to 2FA verification");
                    
                    // Generate and send 2FA code
                    TwoFactorMethod method = user.getPreferredTwoFactorMethod();
                    if (method == null) {
                        method = TwoFactorMethod.EMAIL; // Default fallback
                    }
                    
                    boolean codeSent = twoFactorService.generateAndSendCode(user, method);
                    if (!codeSent) {
                        logger.error("Failed to send 2FA code for user: {}", user.getUsername());
                        response.sendRedirect("/login?error=2fa-failed");
                        return;
                    }
                    
                    // Create 2FA token and set it in security context
                    TwoFactorAuthenticationToken twoFactorToken = new TwoFactorAuthenticationToken(user.getUsername());
                    SecurityContextHolder.getContext().setAuthentication(twoFactorToken);
                    
                    // Redirect to 2FA verification page
                    response.sendRedirect("/auth/2fa");
                    return;
                } else {
                    logger.info("Device is trusted, skipping 2FA verification");
                }
            }
            
            try {
                // Create new session for successfully authenticated user
                sessionService.createSession(user, request);
                logger.info("Session created for user: {}", user.getUsername());
            } catch (Exception e) {
                logger.error("Failed to create session for user: {}. Error: {}", user.getUsername(), e.getMessage());
            }
        } else {
            logger.warn("Principal is not an instance of User: {}", principal.getClass());
        }
        
        response.sendRedirect("/dashboard");
    }
}



