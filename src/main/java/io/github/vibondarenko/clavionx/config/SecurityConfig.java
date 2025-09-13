package io.github.vibondarenko.clavionx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.security.Role;
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

    // DaoAuthenticationProvider bean is not required in Spring Security 6 setup.
    // HttpSecurity#userDetailsService combined with a PasswordEncoder bean will
    // configure the appropriate authentication provider under the hood.
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configure authentication using provided UserDetailsService bean
            // PasswordEncoder bean will be picked up automatically by Spring Security
            .userDetailsService(userDetailsService)
            // Keep 2FA authentication provider
            .authenticationProvider(twoFactorAuthenticationProvider)
            .authorizeHttpRequests(authz -> authz
                // Public resources
                .requestMatchers(Paths.getPublic()).permitAll()
                .requestMatchers(Paths.API_PUBLIC).permitAll()
                
                // 2FA endpoints - allow access during 2FA process
                .requestMatchers(Paths.AUTH_2FA, Paths.AUTH_2FA_ALL).permitAll()
                .requestMatchers(Paths.SETTINGS_2FA_ALL).authenticated()
                
                // Admin access
                .requestMatchers(Paths.ADMIN_ALL, Paths.API_ADMIN_ALL).hasAnyRole(Role.getAdministrativeRoles())
                .requestMatchers(Paths.MANAGE_ALL, Paths.API_MANAGE_ALL).hasAnyRole(Role.MANAGER.name())

                .requestMatchers(Paths.DASHBOARD_ALL).hasAnyRole(Role.getDashboardAccessRoles())

                .requestMatchers(Paths.COURSES_MANAGE_ALL).hasAnyRole(Role.getCourseManagementRoles())
                .requestMatchers(Paths.COURSES_ALL).hasAnyRole(Role.getCourseViewingRoles())

                // Analytics and reports
                .requestMatchers(Paths.ANALYTICS_ALL, Paths.REPORTS_ALL).hasAnyRole(Role.getAnalyticsAccessRoles())

                // Student areas
                .requestMatchers(Paths.STUDENT_ALL).hasAnyRole(Role.getStudentAreaAccessRoles())

                // All other requests require authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage(Paths.LOGIN)
                .loginProcessingUrl(Paths.LOGIN)
                .successHandler(loginSuccessHandler)  // Use our custom handler for all logins
                .failureUrl(Paths.LOGIN_ERROR)
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl(Paths.LOGOUT)
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