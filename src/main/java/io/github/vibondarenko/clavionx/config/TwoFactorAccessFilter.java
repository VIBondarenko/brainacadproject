package io.github.vibondarenko.clavionx.config;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.vibondarenko.clavionx.security.TwoFactorAuthenticationToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter to allow access to 2FA pages for users with TwoFactorAuthenticationToken
 * even if they are not fully authenticated yet
 */
@Component
public class TwoFactorAccessFilter extends OncePerRequestFilter {

    private static final String TWO_FA_PATH = "/auth/2fa";
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        
        // Check if this is a request to 2FA endpoints
        if (requestUri.startsWith(TWO_FA_PATH)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // If user has TwoFactorAuthenticationToken, allow access
            if (auth instanceof TwoFactorAuthenticationToken) {
                // Set a request attribute to indicate this is allowed
                request.setAttribute("twoFactorAccess", true);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}



