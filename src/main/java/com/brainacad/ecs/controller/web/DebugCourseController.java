package com.brainacad.ecs.controller.web;

import com.brainacad.ecs.facade.EducationSystemFacade;
import com.brainacad.ecs.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DebugCourseController {

    @Autowired
    private EducationSystemFacade educationSystemFacade;

    @GetMapping("/test-courses")
    public String testCourses(Model model) {
        System.out.println("=== TEST COURSES ENDPOINT CALLED ===");
        
        try {
            List<Course> courses = educationSystemFacade.getAllCourses();
            System.out.println("Courses retrieved: " + (courses != null ? courses.size() : "null"));
            
            if (courses != null && !courses.isEmpty()) {
                for (Course course : courses) {
                    System.out.println("Course ID: " + course.getId() + ", Name: " + course.getName() + ", Description: " + course.getDescription());
                }
            }
            
            model.addAttribute("courses", courses);
            model.addAttribute("totalCourses", courses != null ? courses.size() : 0);
            model.addAttribute("pageSize", 10);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 1);
            
            System.out.println("Model attributes set:");
            System.out.println("- courses: " + (courses != null ? courses.size() : "null"));
            System.out.println("- totalCourses: " + (courses != null ? courses.size() : 0));
            
            System.out.println("Returning courses/list template");
            return "courses/list";
        } catch (Exception e) {
            System.err.println("Error in test-courses endpoint: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading courses: " + e.getMessage());
            return "error";
        }
    }
}
