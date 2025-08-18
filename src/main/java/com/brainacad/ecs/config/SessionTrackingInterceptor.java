package com.brainacad.ecs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.brainacad.ecs.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Интерцептор для автоматического отслеживания активности пользователя в сессии
 */
@Component
public class SessionTrackingInterceptor implements HandlerInterceptor {

    @Autowired
    private SessionService sessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // Проверяем, что пользователь аутентифицирован
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            
            // Получаем sessionId
            String sessionId = request.getSession().getId();
            
            // Обновляем время последней активности
            sessionService.updateLastActivity(sessionId);
        }
        
        return true;
    }
}
