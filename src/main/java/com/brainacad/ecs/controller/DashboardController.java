package com.brainacad.ecs.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.repository.TaskRepository;
import com.brainacad.ecs.repository.TrainerRepository;
/**
 * Dashboard controller for main application interface
 * Works with JPA entities and Spring Data repositories
 */
@Controller
@RequestMapping("/")
public class DashboardController {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;
    private final TaskRepository taskRepository;

    /**
     * Constructor for DashboardController with required repositories
     */
    public DashboardController(
            CourseRepository courseRepository,
            StudentRepository studentRepository,
            TrainerRepository trainerRepository,
            TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.trainerRepository = trainerRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Dashboard home page
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            com.brainacad.ecs.entity.User user = 
                (com.brainacad.ecs.entity.User) authentication.getPrincipal();
            
            model.addAttribute("pageTitle", "Dashboard");
            model.addAttribute("pageDescription", " Welcome to your dashboard! Here you can get a quick overview of your courses, students, teachers, and recent activities.");
            model.addAttribute("pageIcon", "fa-graduation-cap");

            // Add user info to model
            model.addAttribute("username", user.getUsername());
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("role", user.getRole());
            model.addAttribute("authorities", user.getAuthorities());
            
            // Add statistics
            model.addAttribute("totalCourses", courseRepository.count());
            model.addAttribute("totalStudents", studentRepository.count());
            model.addAttribute("totalTrainers", trainerRepository.count());
            model.addAttribute("totalTasks", taskRepository.count());
            
            return "dashboard/index";
        } else {
            return "redirect:/login";
        }
    }

    /**
     * Admin dashboard with additional statistics
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public String adminDashboard(Model model, Authentication authentication) {
        org.springframework.security.core.userdetails.User user = 
            (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        // Add user info
        model.addAttribute("username", user.getUsername());
        model.addAttribute("authorities", user.getAuthorities());
        
        // Add detailed statistics for admin
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalTrainers", trainerRepository.count());
        model.addAttribute("totalTasks", taskRepository.count());
        
        // Add recent courses (limit to 5)
        model.addAttribute("recentCourses", courseRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .limit(5)
                .toList());
        
        return "dashboard/index";
    }
}
