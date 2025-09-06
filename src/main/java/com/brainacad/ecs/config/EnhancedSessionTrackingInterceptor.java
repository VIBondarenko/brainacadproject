package com.brainacad.ecs.config;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.brainacad.ecs.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Enhanced Session Tracking Interceptor with improved security and monitoring
 */
@Component
public class EnhancedSessionTrackingInterceptor implements HandlerInterceptor {

    private static final Logger logger = Logger.getLogger(EnhancedSessionTrackingInterceptor.class.getName());

    @Value("${ecs.session.track-anonymous:false}")
    private boolean trackAnonymous;

    @Value("${ecs.session.log-activity:false}")
    private boolean logActivity;

    private final SessionService sessionService;

    public EnhancedSessionTrackingInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        
        // Get current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Check if user is authenticated (excluding anonymous users unless configured)
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            
            // Skip anonymous users unless specifically configured to track them
            if ("anonymousUser".equals(username) && !trackAnonymous) {
                return true;
            }
            
            HttpSession session = request.getSession(false);
            if (session != null) {
                String sessionId = session.getId();
                
                // Update last activity time
                sessionService.updateLastActivity(sessionId);
                
                // Log activity if enabled
                if (logActivity && logger.isLoggable(java.util.logging.Level.FINE)) {
                    logger.fine(String.format("Session activity: %s, User: %s, URI: %s", 
                        sessionId, username, request.getRequestURI()));
                }
                
                // Add session info to request attributes for potential use by controllers
                request.setAttribute("CURRENT_SESSION_ID", sessionId);
                request.setAttribute("CURRENT_USER", username);
            }
        }
        
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                @NonNull Object handler, @Nullable Exception ex) throws Exception {
        
        // Log any exceptions that occurred during request processing
        if (ex != null && logger.isLoggable(java.util.logging.Level.WARNING)) {
            String sessionId = (String) request.getAttribute("CURRENT_SESSION_ID");
            logger.warning(String.format("Exception during request processing for session %s: %s", 
                sessionId, ex.getMessage()));
        }
    }
}
