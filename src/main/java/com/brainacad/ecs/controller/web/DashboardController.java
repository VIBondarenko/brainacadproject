package com.brainacad.ecs.controller.web;

import com.brainacad.ecs.facade.EducationSystemFacade;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

/**
 * Dashboard Web Controller
 * 
 * Provides the main dashboard with statistics and quick access to all modules
 * Integrates with existing SOLID architecture through EducationSystemFacade
 */
@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    private static final Logger logger = Logger.getLogger(DashboardController.class.getName());
    private final EducationSystemFacade educationSystem;
    
    public DashboardController(EducationSystemFacade educationSystem) {
        this.educationSystem = educationSystem;
    }
    
    /**
     * Main dashboard page
     */
    @GetMapping
    public String dashboard(Model model) {
        try {
            // Get statistics from existing facade
            model.addAttribute("totalCourses", educationSystem.getTotalCoursesCount());
            model.addAttribute("totalStudents", educationSystem.getTotalStudentsCount());
            model.addAttribute("totalTrainers", educationSystem.getTotalTrainersCount());
            
            // Get recent data for quick view
            model.addAttribute("recentCourses", educationSystem.getAllCourses()
                .stream().limit(5).toList());
            model.addAttribute("recentStudents", educationSystem.getAllStudents()
                .stream().limit(5).toList());
            model.addAttribute("recentTrainers", educationSystem.getAllTrainers()
                .stream().limit(5).toList());
            
            // System status
            model.addAttribute("systemStatus", "ONLINE");
            model.addAttribute("lastDataSave", java.time.LocalDateTime.now().minusMinutes(5));
            
            logger.info("Dashboard loaded successfully");
            return "dashboard/index";
            
        } catch (Exception e) {
            logger.severe("Error loading dashboard: " + e.getMessage());
            model.addAttribute("error", "Error loading dashboard data");
            return "error/500";
        }
    }
    
    /**
     * Quick stats endpoint for updates
     */
    @GetMapping("/stats")
    public String quickStats(Model model) {
        model.addAttribute("totalCourses", educationSystem.getTotalCoursesCount());
        model.addAttribute("totalStudents", educationSystem.getTotalStudentsCount());
        model.addAttribute("totalTrainers", educationSystem.getTotalTrainersCount());
        return "dashboard/stats :: stats-fragment";
    }
}
