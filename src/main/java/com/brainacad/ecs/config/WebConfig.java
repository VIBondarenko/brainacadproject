package com.brainacad.ecs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Web MVC Configuration for Education Control System
 * 
 * Configures basic MVC settings for the application
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * Configure view controllers for simple static pages
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/dashboard");
        registry.addViewController("/about").setViewName("pages/about");
        registry.addViewController("/help").setViewName("pages/help");
    }
}
