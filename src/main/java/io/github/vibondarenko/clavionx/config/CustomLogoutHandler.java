package io.github.vibondarenko.clavionx.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Custom LogoutHandler for saving session ID before invalidation
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutHandler.class);

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Save session ID in request attributes before invalidation
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            request.setAttribute("LOGOUT_SESSION_ID", sessionId);
            logger.info("Saved session ID for logout: {}", sessionId);
        } else {
            logger.warn("No HTTP session found to save for logout");
        }
    }
}



