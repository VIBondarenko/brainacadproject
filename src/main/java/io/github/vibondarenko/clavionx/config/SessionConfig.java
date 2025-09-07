package io.github.vibondarenko.clavionx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Session Configuration for Education Control System
 * Configures session management parameters and security for all environments
 * Uses standard HTTP sessions without Redis complexity
 */
@Configuration
@EnableJpaRepositories(basePackages = "io.github.vibondarenko.clavionx.repository")
public class SessionConfig {

    @Value("${ecs.session.timeout-minutes:30}")
    private int sessionTimeoutMinutes;

    @Value("${ecs.session.cookie-name:JSESSIONID}")
    private String cookieName;

    @Value("${ecs.session.cookie-max-age-seconds:1800}")
    private int cookieMaxAge;

    @Value("${ecs.session.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${ecs.session.cookie-http-only:true}")
    private boolean cookieHttpOnly;

    @Value("${ecs.session.cookie-same-site:Lax}")
    private String cookieSameSite;

    /**
     * Configure cookie SameSite policy for enhanced security
     */
    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofLax(); // Default to Lax for better security
    }

    // Getters for configuration values (used by other components)
    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }

    public String getCookieName() {
        return cookieName;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }

    public boolean isCookieHttpOnly() {
        return cookieHttpOnly;
    }

    public String getCookieSameSite() {
        return cookieSameSite;
    }
}



