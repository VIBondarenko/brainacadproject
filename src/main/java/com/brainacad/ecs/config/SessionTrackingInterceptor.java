package com.brainacad.ecs.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.brainacad.ecs.service.SessionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

/**
 * Interceptor for automatic tracking of user activity in the session
 */
@Component
public class SessionTrackingInterceptor implements HandlerInterceptor {

    private final SessionService sessionService;

    public SessionTrackingInterceptor(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {

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
