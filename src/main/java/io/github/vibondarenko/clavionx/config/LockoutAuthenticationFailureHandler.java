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

@Component
public class LockoutAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final PersistentLoginAttemptService attempts;

    public LockoutAuthenticationFailureHandler(PersistentLoginAttemptService attempts) {
        this.attempts = attempts;
    }

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
