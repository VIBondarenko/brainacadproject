package com.brainacad.ecs.controller.web;

import com.brainacad.ecs.facade.EducationSystemFacade;
import com.brainacad.ecs.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TestDebugController {

    @Autowired
    private EducationSystemFacade educationSystemFacade;

    @GetMapping("/debug-courses")
    public String debugCourses(Model model) {
        System.out.println("=== DEBUG COURSES ENDPOINT CALLED ===");
        
        List<Course> courses = educationSystemFacade.getAllCourses();
        System.out.println("Courses count: " + (courses != null ? courses.size() : "null"));
        
        if (courses != null) {
            for (Course course : courses) {
                System.out.println("Course ID: " + course.getId() + ", Name: " + course.getName());
            }
        }
        
        model.addAttribute("courses", courses);
        model.addAttribute("debug", true);
        
        System.out.println("Model attributes set, returning courses/list template");
        return "courses/debug-list";
    }
}
