package io.github.vibondarenko.clavionx.config;

import java.io.IOException;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.service.PersistentLoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom authentication failure handler that tracks login attempts and locks accounts after
 * a certain number of failed attempts.
 */
@Component
public class LockoutAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final PersistentLoginAttemptService attempts;

    /**
     * Constructor for LockoutAuthenticationFailureHandler.
     *
     * @param attempts the service to track login attempts
     */
    public LockoutAuthenticationFailureHandler(PersistentLoginAttemptService attempts) {
        this.attempts = attempts;
    }
    
    /**
     * Handles authentication failure by recording the failed attempt and redirecting the user
     * to the appropriate error page.
     *
     * @param request   the HttpServletRequest
     * @param response  the HttpServletResponse
     * @param exception the exception that caused the authentication failure
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        String username = request.getParameter("username");
        if (username != null) {
            try { attempts.onFailure(username.toLowerCase()); } catch (Exception ignored) { /* swallow to avoid leaking errors to UI */ }
        }
        if (exception instanceof LockedException) {
            response.sendRedirect(Paths.LOGIN + "?error=locked");
            return;
        }
        if (username != null && attempts.isLocked(username.toLowerCase())) {
            response.sendRedirect(Paths.LOGIN + "?error=locked");
            return;
        }
        response.sendRedirect(Paths.LOGIN + "?error=true");
    }
}