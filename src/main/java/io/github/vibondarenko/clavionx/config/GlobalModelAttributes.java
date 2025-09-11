package io.github.vibondarenko.clavionx.config;

import java.time.Instant;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Global model attributes provider for all controllers
 * Provides application build information to all templates
 */
@ControllerAdvice
public class GlobalModelAttributes {
    
    private final BuildProperties buildProperties;
    
    public GlobalModelAttributes(@Autowired(required = false) BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }
    
    /**
     * Provide build information to all templates
     */
    @ModelAttribute("build")
    public BuildProperties getBuildProperties() {
        // If buildProperties is null, create a fallback with default values
        if (buildProperties == null) {
            Properties props = new Properties();
            props.setProperty("name", "ClavionX");
            props.setProperty("version", "1.0.2");
            props.setProperty("time", Instant.now().toString());
            props.setProperty("description", "Education Management System");
            props.setProperty("developer.name", "Vitaliy Bondarenko");
            props.setProperty("developer.email", "vibondarenko@gmail.com");
            props.setProperty("developer.url", "https://vibondarenko.github.io");
            props.setProperty("application.email", "clavionx@gmail.com");
            
            return new BuildProperties(props);
        }
        return buildProperties;
    }
}
