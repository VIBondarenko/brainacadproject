package io.github.vibondarenko.clavionx.config;

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
    
    public GlobalModelAttributes(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }
    
    /**
     * Provide build information to all templates
     */
    @ModelAttribute("build")
    public BuildProperties getBuildProperties() {
        return buildProperties;
    }
}
