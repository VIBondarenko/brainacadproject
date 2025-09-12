package io.github.vibondarenko.clavionx.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.vibondarenko.clavionx.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Unit tests for EnhancedSessionTrackingInterceptor
 */
@ExtendWith(MockitoExtension.class)
class EnhancedSessionTrackingInterceptorTest {

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private EnhancedSessionTrackingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new EnhancedSessionTrackingInterceptor(sessionService);
        ReflectionTestUtils.setField(interceptor, "trackAnonymous", false);
        ReflectionTestUtils.setField(interceptor, "logActivity", false);
    }

    @Test
    void preHandle_ShouldReturnTrue_WhenNoAuthentication() throws Exception {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(sessionService, never()).updateLastActivity(any());
    }

    @Test
    void preHandle_ShouldReturnTrue_WhenAnonymousUserAndTrackingDisabled() throws Exception {
        // Given
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        SecurityContextHolder.setContext(securityContext);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(sessionService, never()).updateLastActivity(any());
    }

    @Test
    void preHandle_ShouldReturnTrue_WhenValidSession() throws Exception {
        // Given
        String username = "testUser";
        String sessionId = "session123";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn(sessionId);
        SecurityContextHolder.setContext(securityContext);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(sessionService).updateLastActivity(sessionId);
        verify(request).setAttribute("CURRENT_SESSION_ID", sessionId);
        verify(request).setAttribute("CURRENT_USER", username);
    }

    @Test
    void preHandle_ShouldReturnFalse_WhenSessionServiceThrowsException() throws Exception {
        // Given
        String username = "testUser";
        String sessionId = "session123";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn(sessionId);
        doThrow(new RuntimeException("Service error")).when(sessionService).updateLastActivity(sessionId);
        SecurityContextHolder.setContext(securityContext);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertFalse(result);
        verify(sessionService).updateLastActivity(sessionId);
    }

    @Test
    void preHandle_ShouldReturnTrue_WhenNoSession() throws Exception {
        // Given
        String username = "testUser";
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        when(request.getSession(false)).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // When
        boolean result = interceptor.preHandle(request, response, new Object());

        // Then
        assertTrue(result);
        verify(sessionService, never()).updateLastActivity(any());
    }

    @Test
    void afterCompletion_ShouldLogWarning_WhenExceptionOccurred() throws Exception {
        // Given
        Exception exception = new RuntimeException("Test exception");
        when(request.getAttribute("CURRENT_SESSION_ID")).thenReturn("session123");

        // When & Then
        // Verify that afterCompletion completes without throwing an exception
        try {
            interceptor.afterCompletion(request, response, new Object(), exception);
            assertTrue(true, "Method should complete successfully even when exception is provided");
        } catch (Exception e) {
            assertTrue(false, "Method should not throw exception during afterCompletion");
        }
        
        verify(request).getAttribute("CURRENT_SESSION_ID");
    }

    @Test
    void afterCompletion_ShouldNotLog_WhenNoException() throws Exception {
        // When
        interceptor.afterCompletion(request, response, new Object(), null);

        // Then
        // Verify that the method completes successfully without any side effects
        // When there's no exception, getAttribute should not be called
        verify(request, never()).getAttribute("CURRENT_SESSION_ID");
    }
}