package io.github.vibondarenko.clavionx.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

/**
 * OpenAPI Configuration for Education Control System Provides comprehensive API
 * documentation with security integration
 */
@Configuration
public class OpenAPIConfig {
	@Value("${server.servlet.context-path:}")
	private String contextPath;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info()
											.title("Education Control System API")
											.description("""
											Comprehensive Education Management System API
											## Features
											- User management with role-based access control
											- Course management and enrollment
											- Task management and submissions
											- Session tracking and analytics
											- Real-time activity monitoring
											
											## Security
											This API uses role-based authentication with the following roles:
											- **SUPER_ADMIN**: Full system access
											- **ADMIN**: Administrative functions
											- **MANAGER**: Management operations
											- **TEACHER**: Course and student management
											- **STUDENT**: Limited access to own data
											- **ANALYST**: Read-only analytics access
											- **MODERATOR**: Content moderation
											- **GUEST**: Minimal read access
											
											## Getting Started
											1. Authenticate using `/login` endpoint
											2. Use session-based authentication for subsequent requests
											3. Check required permissions for each endpoint
											""")
											.version("1.0.0")
											.contact(new Contact()
											.name("BrainAcad Development Team")
											.email("dev@brainacad.com")
											.url("https://brainacad.com"))
											.license(new License()
											.name("MIT License")
											.url("https://opensource.org/licenses/MIT")))
											.servers(List.of(
												new Server().url("http://localhost:8080" + contextPath)
													.description("Development Server"),
												new Server().url("https://ecs.brainacad.com" + contextPath)
													.description("Production Server")))
											.addSecurityItem(new SecurityRequirement().addList("session-auth"))
											.components(new io.swagger.v3.oas.models.Components()
											.addSecuritySchemes("session-auth", new SecurityScheme()
											.type(SecurityScheme.Type.APIKEY)
											.in(SecurityScheme.In.COOKIE)
											.name("JSESSIONID")
											.description("Session-based authentication using Spring Security")));
	}
}




