package io.github.vibondarenko.clavionx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main Spring Boot Application Class for Education Management System Web Application
 * 
 * Features:
 * - Web interface with Thymeleaf templates
 * - REST API for external integrations
 * - Security with Spring Security
 * - Modern Spring Boot architecture
 * 
 * Architecture:
 * 1. Full Spring Boot application with database persistence
 * 2. JPA entities for data modeling and persistence
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class ClavionXApp {
    
    private static final Logger logger = LoggerFactory.getLogger(ClavionXApp.class);
    
    public static void main(String[] args) {
        logger.info("Starting Education Management System Web Application...");
        
        // Set system properties for better performance
        System.setProperty("spring.jpa.open-in-view", "false");
        System.setProperty("server.servlet.encoding.force", "true");

        SpringApplication app = new SpringApplication(ClavionXApp.class);

        // Add custom banner
        app.setBannerMode(org.springframework.boot.Banner.Mode.CONSOLE);
        
        app.run(args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("""
            =============================================================
            =     ClavionX Learning Management System Started           =
            =============================================================
            = Developer:          Vitaliy Bondarenko                    =
            = Web Interface:      http://localhost:8080                 =
            = API Documentation:  http://localhost:8080/swagger-ui.html =  
            = Database:           PostgreSQL (ecs)                      =
            = Actuator:           http://localhost:8080/actuator/health =
            =============================================================
            """);
    }
}