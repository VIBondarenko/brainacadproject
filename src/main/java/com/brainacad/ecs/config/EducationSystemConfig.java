package com.brainacad.ecs.config;

import com.brainacad.ecs.facade.EducationSystemFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Education System components
 */
@Configuration
public class EducationSystemConfig {
    
    /**
     * Creates EducationSystemFacade as a Spring Bean
     */
    @Bean
    public EducationSystemFacade educationSystemFacade() {
        EducationSystemFacade instance = EducationSystemFacade.getInstance();
        System.out.println("Spring creating EducationSystemFacade bean with " + instance.getAllCourses().size() + " courses");
        
        // Initialize test data if system is empty
        instance.initializeTestDataIfEmpty();
        System.out.println("After initialization: " + instance.getAllCourses().size() + " courses");
        
        return instance;
    }
}
