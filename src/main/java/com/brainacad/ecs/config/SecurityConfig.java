package com.brainacad.ecs.config;

import com.brainacad.ecs.security.Role;
import com.brainacad.ecs.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
 * - Advanced role-based access control with 8-tier hierarchy
 * - Method-level security with custom annotations
 * - Public access to API documentation
 * - PostgreSQL database integration
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    /**
     * Configure HTTP Security with simplified settings for development
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for simplicity in development
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization with role-based access
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/static/**").permitAll()
                .requestMatchers("/actuator/health").permitAll() // Health check
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // API docs
                .requestMatchers("/test/**").permitAll() // All test endpoints
                .requestMatchers("/error").permitAll() // Error page
                // Admin and Management endpoints
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN")
                .requestMatchers("/management/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER")
                // Course management - requires teacher level or higher
                .requestMatchers("/courses/add", "/courses/edit/**", "/courses/delete/**")
                    .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_TEACHER")
                // Analytics access
                .requestMatchers("/analytics/**").hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN", "ROLE_ANALYST")
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
            );
        
        return http.build();
    }
    
    /**
     * Configure users for development with new role hierarchy
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Super Admin - full system access
        UserDetails superAdmin = User.builder()
            .username("superadmin")
            .password(passwordEncoder().encode("super123"))
            .authorities(SecurityUtils.getAuthorities(Role.SUPER_ADMIN))
            .build();
        
        // Admin - administrative access
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("***REMOVED***"))
            .authorities(SecurityUtils.getAuthorities(Role.ADMIN))
            .build();
        
        // Manager - management access
        UserDetails manager = User.builder()
            .username("manager")
            .password(passwordEncoder().encode("manager123"))
            .authorities(SecurityUtils.getAuthorities(Role.MANAGER))
            .build();
        
        // Teacher - educational content management
        UserDetails teacher = User.builder()
            .username("teacher")
            .password(passwordEncoder().encode("teacher123"))
            .authorities(SecurityUtils.getAuthorities(Role.TEACHER))
            .build();
        
        // Student - basic learning access
        UserDetails student = User.builder()
            .username("student")
            .password(passwordEncoder().encode("student123"))
            .authorities(SecurityUtils.getAuthorities(Role.STUDENT))
            .build();
        
        // Analyst - analytics and reporting access
        UserDetails analyst = User.builder()
            .username("analyst")
            .password(passwordEncoder().encode("analyst123"))
            .authorities(SecurityUtils.getAuthorities(Role.ANALYST))
            .build();
        
        return new InMemoryUserDetailsManager(superAdmin, admin, manager, teacher, student, analyst);
    }
    
    /**
     * Password encoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
