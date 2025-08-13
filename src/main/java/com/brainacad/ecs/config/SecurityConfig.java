package com.brainacad.ecs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for Education Control System
 * 
 * Features:
 * - Basic authentication for development
 * - Role-based access control
 * - Public access to API documentation
 * - H2 console access for development
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    /**
     * Configure HTTP Security with simplified settings for development
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity in development
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll() // H2 Console for development
                .requestMatchers("/actuator/health").permitAll() // Health check
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // API docs
                .requestMatchers("/test-courses").permitAll() // Debug endpoint
                .requestMatchers("/test/**").permitAll() // All test endpoints
                .requestMatchers("/debug-courses").permitAll() // Debug courses
                .requestMatchers("/error").permitAll() // Error page
                .anyRequest().authenticated()
            )
            
            // Configure form login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            
            // Configure logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            
            // Allow frames for H2 console
            .headers(headers -> headers
                .frameOptions().sameOrigin()
            );
        
        return http.build();
    }
    
    /**
     * Configure users for development
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("***REMOVED***"))
            .roles("ADMIN", "USER")
            .build();
        
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();
        
        return new InMemoryUserDetailsManager(admin, user);
    }
    
    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
