package com.brainacad.ecs.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.brainacad.ecs.entity.UserActivity;
import com.brainacad.ecs.service.UserActivityService;

/**
 * Web Controller for Activity History Management
 * Provides web interface for viewing user activities and analytics
 */
@Controller
@RequestMapping("/admin/activities")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'ANALYST')")
public class ActivityController {

    private final UserActivityService activityService;

    public ActivityController(UserActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Display activities list with pagination and filtering
     */
    @GetMapping
    public String listActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String actionType,
            @RequestParam(defaultValue = "24") int hours,
            Model model) {

        // Create pageable with sorting
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserActivity> activities;
        
        // Apply filters
        if (userId != null) {
            activities = activityService.getUserActivities(userId, pageable);
            model.addAttribute("selectedUserId", userId);
        } else if (actionType != null && !actionType.isEmpty()) {
            activities = activityService.getActivitiesByActionType(actionType, pageable);
            model.addAttribute("selectedActionType", actionType);
        } else {
            // Get recent activities if no specific filter
            List<UserActivity> recentList = activityService.getRecentActivities(hours);
            // Convert to page for consistent interface
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), recentList.size());
            List<UserActivity> pageContent = recentList.subList(start, end);
            activities = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, recentList.size());
        }

        // Get statistics for dashboard
        List<Object[]> stats = activityService.getActivityStatistics();
        List<Object[]> topUsers = activityService.getTopActiveUsers(7);

        model.addAttribute("pageTitle", "Activity History");
        model.addAttribute("pageDescription", "View and manage user activity logs");
        model.addAttribute("pageIcon", "fa-history");

        model.addAttribute("activities", activities);
        model.addAttribute("statistics", stats);
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", activities.getTotalPages());
        model.addAttribute("totalElements", activities.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("hours", hours);

        return "admin/activities/list";
    }

    /**
     * Display user-specific activities
     */
    @GetMapping("/user/{userId}")
    public String userActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, 
            Sort.by("timestamp").descending());
        Page<UserActivity> activities = activityService.getUserActivities(userId, pageable);

        model.addAttribute("activities", activities);
        model.addAttribute("userId", userId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", activities.getTotalPages());

        return "admin/activities/user-activities";
    }

    /**
     * Display activity analytics dashboard
     */
    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'ANALYST')")
    public String analytics(Model model) {
        
        // Get comprehensive analytics data
        List<Object[]> stats = activityService.getActivityStatistics();
        List<Object[]> topUsers = activityService.getTopActiveUsers(30);
        List<UserActivity> recentActivities = activityService.getRecentActivities(24);

        model.addAttribute("pageTitle", "Analytics");
        model.addAttribute("pageDescription", "Show Analytics");
        model.addAttribute("pageIcon", "fa-chart-bar");

        model.addAttribute("statistics", stats);
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("recentActivities", recentActivities);

        return "admin/activities/analytics";
    }

    /**
     * Security monitoring page
     */
    @GetMapping("/security")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public String securityMonitoring(
            @RequestParam(required = false) String ipAddress,
            @RequestParam(defaultValue = "24") int hours,
            Model model) {

        if (ipAddress != null && !ipAddress.isEmpty()) {
            List<UserActivity> suspiciousActivities = 
                activityService.getSuspiciousActivities(ipAddress, hours);
            model.addAttribute("suspiciousActivities", suspiciousActivities);
            model.addAttribute("searchedIp", ipAddress);
        } else {
            model.addAttribute("searchedIp", null);
        }

        model.addAttribute("pageTitle", "Security Monitoring");
        model.addAttribute("pageDescription", "Monitor suspicious activities and potential security threats");
        model.addAttribute("pageIcon", "fa-chart-bar");

        model.addAttribute("hours", hours);
        return "admin/activities/security";
    }

    /**
     * Cleanup old activities
     */
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public String cleanupOldActivities(
            @RequestParam(defaultValue = "90") int daysToKeep,
            Model model) {

        try {
            activityService.cleanupOldActivities(daysToKeep);
            model.addAttribute("message", 
                "Successfully cleaned up activities older than " + daysToKeep + " days");
            model.addAttribute("messageType", "success");
        } catch (Exception e) {
            model.addAttribute("message", "Error during cleanup: " + e.getMessage());
            model.addAttribute("messageType", "error");
        }

        return "redirect:/admin/activities?message=cleanup";
    }
}
