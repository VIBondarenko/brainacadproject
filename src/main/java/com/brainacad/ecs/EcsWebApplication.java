package com.brainacad.ecs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main Spring Boot Application Class for Education Control System Web Application
 * 
 * Features:
 * - Web interface with Thymeleaf templates
 * - REST API for external integrations
 * - H2 Database integration with JPA
 * - Security with Spring Security
 * - Modern Spring Boot architecture
 * 
 * Architecture:
 * 1. Full Spring Boot application with database persistence
 * 2. Uses H2 in-memory database for development
 * 3. JPA entities for data modeling and persistence
 */
@SpringBootApplication
@EnableTransactionManagement
public class EcsWebApplication {
    
    private static final Logger logger = Logger.getLogger(EcsWebApplication.class.getName());
    
    public static void main(String[] args) {
        logger.log(Level.INFO, "Starting Education Control System Web Application...");
        
        // Set system properties for better performance
        System.setProperty("spring.jpa.open-in-view", "false");
        System.setProperty("server.servlet.encoding.force", "true");
        
        SpringApplication app = new SpringApplication(EcsWebApplication.class);
        
        // Add custom banner
        app.setBannerMode(org.springframework.boot.Banner.Mode.CONSOLE);
        
        app.run(args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.log(Level.INFO, """

            ========================================================
            = EDUCATION CONTROL SYSTEM WEB APPLICATION STARTED     =
            ========================================================
            
            Web Interface: http://localhost:8080
            API Documentation: http://localhost:8080/swagger-ui.html  
            Database: PostgreSQL (ecs)
            Actuator: http://localhost:8080/actuator/health
            
            ========================================================
            """);
    }
}
