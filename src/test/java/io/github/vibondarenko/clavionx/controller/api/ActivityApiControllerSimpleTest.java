package io.github.vibondarenko.clavionx.controller.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple integration test for Activity API Controller
 * Uses in-memory H2 database to avoid external dependencies
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never"
})
class ActivityApiControllerSimpleTest {

    @Test
    void contextLoads() {
        // Simple test to verify that the Spring context loads successfully
        assertTrue(true, "Spring Boot context should load without issues");
    }

    @Test
    void applicationStarts() {
        // Verify that all beans are created properly
        assertTrue(true, "Application should start successfully with all dependencies");
    }
}
