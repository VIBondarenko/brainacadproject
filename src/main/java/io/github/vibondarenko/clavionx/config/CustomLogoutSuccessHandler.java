package io.github.vibondarenko.clavionx.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import io.github.vibondarenko.clavionx.service.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom logout success handler
 */
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    private final SessionService sessionService;

    /**
     * Constructor with SessionService injection
     * 
     * @param sessionService the session service
     */
    public CustomLogoutSuccessHandler(SessionService sessionService) {
        this.sessionService = sessionService;
        logger.info("CustomLogoutSuccessHandler created with SessionService: {}", sessionService);
    }

    /**
     * Handle logout success
     * @param request the HTTP request
     * @param response the HTTP response
     * @param authentication the authentication object
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, 
                                Authentication authentication) throws IOException, ServletException {
        
    logger.info("Logout process started");
        
        // Get sessionId from request attributes (saved by our LogoutHandler)
        String sessionId = (String) request.getAttribute("LOGOUT_SESSION_ID");
        
        if (sessionId != null) {
            logger.info("Found session ID from request attribute: {}", sessionId);
            
            try {
                // Terminate session
                sessionService.terminateSession(sessionId);
                logger.info("Session terminated successfully: {}", sessionId);
                
            } catch (Exception e) {
                logger.error("Failed to terminate session: {}. Error: {}", sessionId, e.getMessage());
            }
        } else {
            logger.warn("No session ID found in request attributes during logout");
            
            // Try alternative approach through authentication
            if (authentication != null) {
                logger.warn("Attempting to find and terminate session by username: {}", authentication.getName());
                // We could add a method to find sessions by username here,
                // but for now just log it
            }
        }
        
        // Log authentication information
        if (authentication != null) {
            logger.info("Logging out user: {}", authentication.getName());
        } else {
            logger.warn("No authentication object found during logout");
        }
        
        // Redirect to login page
        response.sendRedirect("/login?logout=true");
    }
}