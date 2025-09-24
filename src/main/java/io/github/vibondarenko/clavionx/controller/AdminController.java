package io.github.vibondarenko.clavionx.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.vibondarenko.clavionx.dto.SessionDetailsDto;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserSession;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations;
import io.github.vibondarenko.clavionx.service.SessionService;
import io.github.vibondarenko.clavionx.view.ViewAttributes;
/**
 * Controller for administrative functions including session management.
 * Only accessible to users with SUPER_ADMIN or ADMIN role.
 */
@Controller
@RequestMapping("/admin")
@SecurityAnnotations.AdminOnly
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final SessionService sessionService;
    private final UserRepository userRepository;

    /**
     * Constructor for AdminController.
     *
     * @param sessionService service for session management
     * @param userRepository repository for user data
     */
    public AdminController(SessionService sessionService, UserRepository userRepository) {
        this.sessionService = sessionService;
        this.userRepository = userRepository;
    }

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

        model.addAttribute(ViewAttributes.PAGE_TITLE, "Admin Dashboard");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Session Management, System Monitoring, Statistics");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-shield-alt");
        
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
    public String viewSessions(@RequestParam(value = "status", defaultValue = "active") String status, Model model) {
        logger.info("Viewing sessions with status: {}", status);
        
        List<UserSession> sessions;

        sessions = switch (status.toLowerCase()) {
            case "inactive" -> sessionService.getSessionsByStatus(false);
            case "all" -> sessionService.getAllSessions();
            default -> {
                status = "active";
                yield sessionService.getActiveSessions();
            }
        };

        model.addAttribute("sessions", sessions);
        model.addAttribute("currentStatus", status);
        
        // Create a map of userId to username for easy lookup in template
        Map<Long, String> userIdToUsernameMap = new HashMap<>();
        Set<Long> userIds = sessions.stream()
                .map(UserSession::getUserId)
                .collect(Collectors.toSet());
        
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            userIdToUsernameMap.put(user.getId(), user.getUsername());
        }

        model.addAttribute(ViewAttributes.PAGE_TITLE, "Sessions");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Session Management");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-list-ul");
        
        model.addAttribute("userIdToUsernameMap", userIdToUsernameMap);

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
        
        // Create a map of userId to username for easy lookup in template
        Map<Long, String> userIdToUsernameMap = new HashMap<>();
        Set<Long> userIds = userSessions.stream()
                .map(UserSession::getUserId)
                .collect(Collectors.toSet());
        
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            userIdToUsernameMap.put(user.getId(), user.getUsername());
        }
        
        model.addAttribute("userIdToUsernameMap", userIdToUsernameMap);
        
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
        redirectAttributes.addFlashAttribute(io.github.vibondarenko.clavionx.view.ViewAttributes.SUCCESS_MESSAGE, 
                    "Session " + sessionId + " successfully terminated");
        } catch (Exception e) {
        logger.error("Error terminating session: {}", sessionId, e);
        redirectAttributes.addFlashAttribute(io.github.vibondarenko.clavionx.view.ViewAttributes.ERROR_MESSAGE, 
            "Error terminating session: " + e.getMessage());
        }

    return Paths.REDIRECT_ADMIN_SESSIONS;
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
            
        redirectAttributes.addFlashAttribute(io.github.vibondarenko.clavionx.view.ViewAttributes.SUCCESS_MESSAGE, 
                    "All sessions for user " + userId + " successfully terminated (" + userSessions.size() + " sessions)");
        } catch (Exception e) {
        logger.error("Error terminating sessions for user: {}", userId, e);
        redirectAttributes.addFlashAttribute(io.github.vibondarenko.clavionx.view.ViewAttributes.ERROR_MESSAGE, 
            "Error terminating sessions for user: " + e.getMessage());
        }

    return Paths.REDIRECT_ADMIN_SESSIONS;
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
        redirectAttributes.addFlashAttribute(io.github.vibondarenko.clavionx.view.ViewAttributes.SUCCESS_MESSAGE, 
                    "Cleanup completed. Inactive sessions removed: " + cleanedSessions);
        } catch (Exception e) {
        logger.error("Error during session cleanup", e);
        redirectAttributes.addFlashAttribute(io.github.vibondarenko.clavionx.view.ViewAttributes.ERROR_MESSAGE, 
            "Error cleaning up sessions: " + e.getMessage());
        }

    return Paths.REDIRECT_ADMIN_SESSIONS;
    }

    /**
     * Get session details.
     *
     * @param sessionId session ID
     * @return session details as JSON
     */
    @GetMapping("/sessions/{sessionId}/details")
    @ResponseBody
    public SessionDetailsDto getSessionDetails(@PathVariable String sessionId) {
        logger.debug("Getting details for session: {}", sessionId);
        
        UserSession session = sessionService.getSessionBySessionId(sessionId);
        if (session == null) {
            return null;
        }
        
        // Get username for the session
        String username = userRepository.findById(session.getUserId())
                .map(User::getUsername)
                .orElse("Unknown User");
        
        return new SessionDetailsDto(session, username);
    }
}



