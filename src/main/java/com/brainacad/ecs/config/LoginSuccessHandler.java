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

    @Autowired
    private SessionService sessionService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, 
                                      Authentication authentication) throws IOException, ServletException {
        
        // Получаем пользователя из аутентификации
        Object principal = authentication.getPrincipal();
        logger.info("Authentication principal class: " + principal.getClass().getName());
        
        if (principal instanceof User) {
            User user = (User) principal;
            
            logger.info("User ID: " + user.getId());
            logger.info("User class: " + user.getClass().getName());
            logger.info("User name: " + user.getName());
            logger.info("User username: " + user.getUsername());
            
            try {
                // Создаем новую сессию
                sessionService.createSession(user, request);
                logger.info("Session created for user: " + user.getUsername());
                
            } catch (Exception e) {
                logger.severe("Failed to create session for user: " + user.getUsername() + 
                            ". Error: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            logger.warning("Principal is not an instance of User: " + principal.getClass());
        }
        
        // Перенаправляем на дашборд
        response.sendRedirect("/dashboard");
    }
}
