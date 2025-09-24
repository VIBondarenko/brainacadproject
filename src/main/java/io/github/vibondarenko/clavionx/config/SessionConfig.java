package io.github.vibondarenko.clavionx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Session Configuration for Education Management System
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
     * Sets SameSite attribute to Lax by default
     * @return CookieSameSiteSupplier with specified SameSite attribute
     */
    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofLax(); // Default to Lax for better security
    }

    /**
     * Get the session timeout in minutes
     * @return the session timeout in minutes
     */
    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }

    /**
     * Get the name of the session cookie
     * @return the name of the session cookie
     */
    public String getCookieName() {
        return cookieName;
    }
    /**
     * Get the maximum age of the session cookie in seconds
     * @return the maximum age of the session cookie in seconds
     */
    public int getCookieMaxAge() {
        return cookieMaxAge;
    }
    /**
     * Check if the session cookie is marked as secure
     * @return true if the session cookie is secure, false otherwise
     */
    public boolean isCookieSecure() {
        return cookieSecure;
    }

    /**
     * Check if the session cookie is marked as HTTP-only
     * @return true if the session cookie is HTTP-only, false otherwise
     */
    public boolean isCookieHttpOnly() {
        return cookieHttpOnly;
    }
    /**
     * Get the SameSite attribute of the session cookie
     * @return the SameSite attribute of the session cookie
     */    
    public String getCookieSameSite() {
        return cookieSameSite;
    }
}