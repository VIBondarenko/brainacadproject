package io.github.vibondarenko.clavionx.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for loading .env file properties into Spring Environment.
 * This allows using environment variables from .env file in application.yml
 */
public class DotEnvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static final Logger logger = LoggerFactory.getLogger(DotEnvConfig.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            // Load .env file from project root
            Dotenv dotenv = Dotenv.configure()
                    .directory(".")
                    .ignoreIfMissing()
                    .load();
            
            // Convert to Map for Spring PropertySource
            Map<String, Object> envMap = new HashMap<>();
            dotenv.entries().forEach(entry -> 
                envMap.put(entry.getKey(), entry.getValue())
            );
            
            // Add to Spring Environment with high priority
            environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv", envMap)
            );
            
        } catch (IllegalStateException | SecurityException e) {
            // Log error but don't fail application startup
            logger.warn("Could not load .env file: {}", e.getMessage());
        }
    }
}



