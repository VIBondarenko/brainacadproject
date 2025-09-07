
package io.github.vibondarenko.clavionx.config;

import org.springframework.beans.factory.annotation.Value;
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

import io.github.vibondarenko.clavionx.security.TwoFactorAuthenticationProvider;
import io.github.vibondarenko.clavionx.service.CustomUserDetailsService;

/**
 * Security Configuration for Education Management System
 * Implements proper authentication with encrypted passwords and database users
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${security.remember-me.validity-seconds:86400}")
    private int rememberMeValiditySeconds;
    
    @Value("${ecs.session.max-sessions-per-user:5}")
    private int maxSessionsPerUser;

    private final CustomUserDetailsService userDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final CustomLogoutHandler logoutHandler;
    private final TwoFactorAuthenticationProvider twoFactorAuthenticationProvider;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            LoginSuccessHandler loginSuccessHandler,
            CustomLogoutSuccessHandler logoutSuccessHandler,
            CustomLogoutHandler logoutHandler,
            TwoFactorAuthenticationProvider twoFactorAuthenticationProvider) {
        this.userDetailsService = userDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.logoutHandler = logoutHandler;
        this.twoFactorAuthenticationProvider = twoFactorAuthenticationProvider;
    }

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
            .authenticationProvider(twoFactorAuthenticationProvider)
            .authorizeHttpRequests(authz -> authz
                // Public resources
                .requestMatchers("/", "/login", "/activate", "/register", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/forgot-password", "/reset-password").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                
                // 2FA endpoints - allow access during 2FA process
                .requestMatchers("/auth/2fa", "/auth/2fa/**").permitAll()
                .requestMatchers("/settings/2fa/**").authenticated()
                
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
                .successHandler(loginSuccessHandler)  // Use our custom handler for all logins
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
                .sessionFixation().migrateSession()  // Additional protection against session fixation
                .maximumSessions(maxSessionsPerUser)  // We use a configurable value
                .maxSessionsPreventsLogin(false)
            )

            // Remember-me: 1 day (86400 seconds), if the user selected "Remember me"
            .rememberMe(remember -> remember
                .key("educationSystemRememberMe")
                .tokenValiditySeconds(rememberMeValiditySeconds)
                .userDetailsService(userDetailsService)
            );

        return http.build();
    }
}



