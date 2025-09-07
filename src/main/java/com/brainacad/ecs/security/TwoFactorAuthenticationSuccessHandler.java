package com.brainacad.ecs.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom success handler for authentication that handles 2FA flow
 */
@Component
public class TwoFactorAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        // Check if this is a 2FA token that requires verification
        if (authentication instanceof TwoFactorAuthenticationToken) {
            TwoFactorAuthenticationToken token = (TwoFactorAuthenticationToken) authentication;
            
            if (token.requiresTwoFactor()) {
                // User needs to provide 2FA code - redirect to 2FA page
                response.sendRedirect("/auth/2fa");
                return;
            }
        }
        
        // Normal authentication success - redirect to default success URL
        String targetUrl = determineTargetUrl(request, response);
        response.sendRedirect(targetUrl);
    }
    
    /**
     * Determine the target URL after successful authentication
     */
    private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        // Check if there's a saved request (where user was trying to go)
        String savedRequest = (String) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST_URL");
        if (savedRequest != null) {
            request.getSession().removeAttribute("SPRING_SECURITY_SAVED_REQUEST_URL");
            return savedRequest;
        }
        
        // Default redirect
        return "/dashboard";
    }
}
