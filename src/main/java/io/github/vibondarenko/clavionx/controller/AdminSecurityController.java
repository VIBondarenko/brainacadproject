package io.github.vibondarenko.clavionx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.vibondarenko.clavionx.service.PersistentLoginAttemptService;

/**
 * Controller for administrative security operations.
 * Allows users with ADMIN or SUPER_ADMIN roles to unlock user accounts.
 */
@RestController
@RequestMapping("/api/admin/security")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminSecurityController {
    private final PersistentLoginAttemptService loginAttemptService;

    /**
     * Constructor for AdminSecurityController.
     *
     * @param loginAttemptService Service to manage login attempts and account locking.
     */
    public AdminSecurityController(PersistentLoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }
    
    /**
     * Unlocks a user account by user ID.
     *
     * @param userId The ID of the user to unlock.
     * @return A ResponseEntity with no content if successful.
     */
    @PostMapping("/unlock/{userId}")
    public ResponseEntity<Void> unlockUser(@PathVariable Long userId) {
        loginAttemptService.adminUnlock(userId);
        return ResponseEntity.noContent().build();
    }
}
