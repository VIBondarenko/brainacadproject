package io.github.vibondarenko.clavionx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.vibondarenko.clavionx.service.PersistentLoginAttemptService;

@RestController
@RequestMapping("/api/admin/security")
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminSecurityController {
    private final PersistentLoginAttemptService loginAttemptService;

    public AdminSecurityController(PersistentLoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @PostMapping("/unlock/{userId}")
    public ResponseEntity<Void> unlockUser(@PathVariable Long userId) {
        loginAttemptService.adminUnlock(userId);
        return ResponseEntity.noContent().build();
    }
}
