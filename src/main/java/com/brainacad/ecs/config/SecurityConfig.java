package com.brainacad.ecs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.brainacad.ecs.service.CustomUserDetailsService;

/**
 * Security Configuration for Education Control System
 * Implements proper authentication with encrypted passwords and database users
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    
    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler;
    
    @Autowired
    private CustomLogoutHandler logoutHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12 for good security
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(authz -> authz
                // Public resources
                .requestMatchers("/", "/login", "/activate", "/register", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // Admin access
                .requestMatchers("/admin/**", "/api/admin/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                .requestMatchers("/manage/**", "/api/manage/**").hasAnyRole("SUPER_ADMIN", "ADMIN")
                
                // Educational management
                .requestMatchers("/dashboard/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEACHER", "ANALYST", "MODERATOR", "STUDENT")
                .requestMatchers("/courses/manage/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEACHER")
                .requestMatchers("/courses/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEACHER", "STUDENT")
                
                // Analytics and reports
                .requestMatchers("/analytics/**", "/reports/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "ANALYST")
                
                // Student areas
                .requestMatchers("/student/**").hasAnyRole("SUPER_ADMIN", "ADMIN", "MANAGER", "TEACHER", "STUDENT")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(loginSuccessHandler)
                .failureUrl("/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .rememberMe(remember -> remember
                .key("educationSystemRememberMe")
                .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 days
                .userDetailsService(userDetailsService)
            );

        return http.build();
    }
}
