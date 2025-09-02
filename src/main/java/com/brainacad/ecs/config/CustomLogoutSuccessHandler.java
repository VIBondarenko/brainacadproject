package com.brainacad.ecs.config;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.brainacad.ecs.service.SessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom logout success handler
 * Terminates user session on logout
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger logger = Logger.getLogger(CustomLogoutSuccessHandler.class.getName());

    private final SessionService sessionService;

    public CustomLogoutSuccessHandler(SessionService sessionService) {
        this.sessionService = sessionService;
        logger.log(java.util.logging.Level.INFO, "CustomLogoutSuccessHandler created with SessionService: {0}", sessionService);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, 
                                Authentication authentication) throws IOException, ServletException {
        
        logger.info("Logout process started");
        
        // Get sessionId from request attributes (saved by our LogoutHandler)
        String sessionId = (String) request.getAttribute("LOGOUT_SESSION_ID");
        
        if (sessionId != null) {
            logger.log(java.util.logging.Level.INFO, "Found session ID from request attribute: {0}", sessionId);
            
            try {
                // Terminate session
                sessionService.terminateSession(sessionId);
                logger.log(java.util.logging.Level.INFO, "Session terminated successfully: {0}", sessionId);
                
            } catch (Exception e) {
                logger.log(java.util.logging.Level.SEVERE, "Failed to terminate session: {0}. Error: {1}", new Object[]{sessionId, e.getMessage()});
            }
        } else {
            logger.warning("No session ID found in request attributes during logout");
            
            // Try alternative approach through authentication
            if (authentication != null) {
                logger.log(java.util.logging.Level.WARNING, "Attempting to find and terminate session by username: {0}", authentication.getName());
                // We could add a method to find sessions by username here,
                // but for now just log it
            }
        }
        
        // Log authentication information
        if (authentication != null) {
            logger.log(java.util.logging.Level.INFO, "Logging out user: {0}", authentication.getName());
        } else {
            logger.warning("No authentication object found during logout");
        }
        
        // Redirect to login page
        response.sendRedirect("/login?logout=true");
    }
}
