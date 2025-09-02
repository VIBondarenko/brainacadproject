package com.brainacad.ecs.config;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.service.SessionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Authentication success handler
 * Creates new user session on login
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = Logger.getLogger(LoginSuccessHandler.class.getName());

    private final SessionService sessionService;

    public LoginSuccessHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        // Получаем пользователя из аутентификации
        Object principal = authentication.getPrincipal();
        logger.log(java.util.logging.Level.INFO, "Authentication principal class: {0}", principal.getClass().getName());
        
        if (principal instanceof User user) {
            
            logger.log(java.util.logging.Level.INFO, "User ID: {0}", user.getId());
            logger.log(java.util.logging.Level.INFO, "User class: {0}", user.getClass().getName());
            logger.log(java.util.logging.Level.INFO, "User name: {0}", user.getName());
            logger.log(java.util.logging.Level.INFO, "User username: {0}", user.getUsername());
            
            try {
                // Создаем новую сессию
                sessionService.createSession(user, request);
                logger.log(java.util.logging.Level.INFO, "Session created for user: {0}", user.getUsername());
            } catch (Exception e) {
                logger.log(java.util.logging.Level.SEVERE, "Failed to create session for user: {0}. Error: {1}", new Object[]{user.getUsername(), e.getMessage()});
            }
        } else {
            logger.log(java.util.logging.Level.WARNING, "Principal is not an instance of User: {0}", principal.getClass());
        }
        
        response.sendRedirect("/dashboard");
    }
}
