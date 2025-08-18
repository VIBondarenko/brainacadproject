package com.brainacad.ecs.config;

import java.util.logging.Logger;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Кастомный LogoutHandler для сохранения session ID перед инвалидацией
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private static final Logger logger = Logger.getLogger(CustomLogoutHandler.class.getName());

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Сохраняем session ID в атрибутах запроса перед инвалидацией
        HttpSession session = request.getSession(false);
        if (session != null) {
            String sessionId = session.getId();
            request.setAttribute("LOGOUT_SESSION_ID", sessionId);
            logger.info("Saved session ID for logout: " + sessionId);
        } else {
            logger.warning("No HTTP session found to save for logout");
        }
    }
}
