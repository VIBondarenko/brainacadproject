package com.brainacad.ecs.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brainacad.ecs.entity.UserSession;
import com.brainacad.ecs.service.SessionService;

/**
 * Controller for administrative functions including session management.
 * Only accessible to users with SUPER_ADMIN role.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private SessionService sessionService;

    /**
     * Display admin dashboard with session management options.
     *
     * @param model Spring MVC model
     * @return admin dashboard template
     */
    @GetMapping
    public String adminDashboard(Model model) {
        logger.info("Admin dashboard accessed");
        
        // Get session statistics
        List<UserSession> activeSessions = sessionService.getActiveSessions();
        int totalActiveSessions = activeSessions.size();
        
        // Count unique users with active sessions
        long uniqueActiveUsers = activeSessions.stream()
                .mapToLong(UserSession::getUserId)
                .distinct()
                .count();
        
        model.addAttribute("totalActiveSessions", totalActiveSessions);
        model.addAttribute("uniqueActiveUsers", uniqueActiveUsers);
        
        return "admin/dashboard";
    }

    /**
     * Display all active user sessions.
     *
     * @param model Spring MVC model
     * @return sessions list template
     */
    @GetMapping("/sessions")
    public String viewSessions(Model model) {
        logger.info("Viewing active sessions");
        
        List<UserSession> activeSessions = sessionService.getActiveSessions();
        model.addAttribute("sessions", activeSessions);
        
        return "admin/sessions";
    }

    /**
     * Display sessions for a specific user.
     *
     * @param userId user ID to filter sessions
     * @param model Spring MVC model
     * @return sessions list template
     */
    @GetMapping("/sessions/user/{userId}")
    public String viewUserSessions(@PathVariable Long userId, Model model) {
        logger.info("Viewing sessions for user: {}", userId);
        
        List<UserSession> userSessions = sessionService.getUserActiveSessions(userId);
        model.addAttribute("sessions", userSessions);
        model.addAttribute("userId", userId);
        
        return "admin/sessions";
    }

    /**
     * Terminate a specific session.
     *
     * @param sessionId session ID to terminate
     * @param redirectAttributes for flash messages
     * @return redirect to sessions list
     */
    @PostMapping("/sessions/{sessionId}/terminate")
    public String terminateSession(@PathVariable String sessionId, 
                                   RedirectAttributes redirectAttributes) {
        try {
            logger.info("Terminating session: {}", sessionId);
            sessionService.terminateSession(sessionId);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Сессия " + sessionId + " успешно завершена");
        } catch (Exception e) {
            logger.error("Error terminating session: {}", sessionId, e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Ошибка при завершении сессии: " + e.getMessage());
        }
        
        return "redirect:/admin/sessions";
    }

    /**
     * Terminate all sessions for a specific user.
     *
     * @param userId user ID whose sessions to terminate
     * @param redirectAttributes for flash messages
     * @return redirect to sessions list
     */
    @PostMapping("/sessions/user/{userId}/terminate-all")
    public String terminateUserSessions(@PathVariable Long userId, 
                                        RedirectAttributes redirectAttributes) {
        try {
            logger.info("Terminating all sessions for user: {}", userId);
            List<UserSession> userSessions = sessionService.getUserActiveSessions(userId);
            
            for (UserSession session : userSessions) {
                sessionService.terminateSession(session.getSessionId());
            }
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Все сессии пользователя " + userId + " завершены (" + userSessions.size() + " сессий)");
        } catch (Exception e) {
            logger.error("Error terminating sessions for user: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Ошибка при завершении сессий пользователя: " + e.getMessage());
        }
        
        return "redirect:/admin/sessions";
    }

    /**
     * Clean up inactive sessions (older than configured timeout).
     *
     * @param redirectAttributes for flash messages
     * @return redirect to sessions list
     */
    @PostMapping("/sessions/cleanup")
    public String cleanupSessions(RedirectAttributes redirectAttributes) {
        try {
            logger.info("Starting session cleanup");
            int cleanedSessions = sessionService.cleanupInactiveSessions();
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Очистка завершена. Удалено неактивных сессий: " + cleanedSessions);
        } catch (Exception e) {
            logger.error("Error during session cleanup", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Ошибка при очистке сессий: " + e.getMessage());
        }
        
        return "redirect:/admin/sessions";
    }

    /**
     * Get session details via AJAX.
     *
     * @param sessionId session ID
     * @return session details as JSON
     */
    @GetMapping("/sessions/{sessionId}/details")
    @ResponseBody
    public UserSession getSessionDetails(@PathVariable String sessionId) {
        logger.debug("Getting details for session: {}", sessionId);
        return sessionService.getSessionBySessionId(sessionId);
    }
}
