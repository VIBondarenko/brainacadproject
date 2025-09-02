package com.brainacad.ecs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

/**
 * Web MVC Configuration for Education Control System
 * 
 * Configures basic MVC settings for the application
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final SessionTrackingInterceptor sessionTrackingInterceptor;

    public WebConfig(SessionTrackingInterceptor sessionTrackingInterceptor) {
        this.sessionTrackingInterceptor = sessionTrackingInterceptor;
    }
    
    /**
     * Configure view controllers for simple static pages
     */
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/dashboard");
        registry.addViewController("/about").setViewName("pages/about");
        registry.addViewController("/help").setViewName("pages/help");
    }
    
    /**
     * Register interceptors
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(sessionTrackingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/auth/**", "/css/**", "/js/**", "/images/**");
    }
}
