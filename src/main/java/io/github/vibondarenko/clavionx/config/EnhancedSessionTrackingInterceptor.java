package io.github.vibondarenko.clavionx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import io.github.vibondarenko.clavionx.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Enhanced Session Tracking Interceptor with improved security and monitoring
 */
@Component
public class EnhancedSessionTrackingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedSessionTrackingInterceptor.class);

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
                
                try {
                    // Update last activity time
                    sessionService.updateLastActivity(sessionId);
                    
                    // Log activity if enabled
                    if (logActivity) {
                        logger.debug("Session activity: {}, User: {}, URI: {}", sessionId, username, request.getRequestURI());
                    }
                    
                    // Add session info to request attributes for potential use by controllers
                    request.setAttribute("CURRENT_SESSION_ID", sessionId);
                    request.setAttribute("CURRENT_USER", username);
                    
                    return true;
                } catch (Exception ex) {
                    // Log error and decide whether to continue or block the request
                    logger.error("Failed to update session activity for session {}: {}", sessionId, ex.getMessage());
                    // Return false to block the request if session service fails critically
                    return false;
                }
            } else {
                // No session found for authenticated user - this might be suspicious
                logger.warn("No session found for authenticated user: {}", username);
                // Allow request to continue but log the issue
                return true;
            }
        }
        
        // Allow unauthenticated requests to continue
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                @NonNull Object handler, @Nullable Exception ex) throws Exception {
        
        // Log any exceptions that occurred during request processing
        if (ex != null) {
            String sessionId = (String) request.getAttribute("CURRENT_SESSION_ID");
            logger.warn("Exception during request processing for session {}: {}", sessionId, ex.getMessage());
        }
    }
}



