package io.github.vibondarenko.clavionx.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.UserLoginSecurity;
import io.github.vibondarenko.clavionx.repository.UserLoginSecurityRepository;
import io.github.vibondarenko.clavionx.repository.UserRepository;

/**
 * Service to manage persistent login attempts and account locking.
 */
@Service
public class PersistentLoginAttemptService {
    private final int maxAttempts;
    private final int windowMinutes;
    private final int lockMinutes;
    private final UserRepository userRepository;
    private final UserLoginSecurityRepository securityRepository;

    public PersistentLoginAttemptService(
        UserRepository userRepository,
        UserLoginSecurityRepository securityRepository,
        @Value("${security.lockout.max-attempts:5}") int maxAttempts,
        @Value("${security.lockout.window-minutes:15}") int windowMinutes,
        @Value("${security.lockout.lock-minutes:15}") int lockMinutes) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.maxAttempts = maxAttempts;
        this.windowMinutes = windowMinutes;
        this.lockMinutes = lockMinutes;
    }

    /**
     * Reset login attempts on successful login for the user identified by username or email.
     * @param usernameOrEmail username or email
     */
    @Transactional
    public void onSuccess(String usernameOrEmail) {
        userRepository.findByUsernameOrEmail(usernameOrEmail)
            .ifPresent(user -> securityRepository.findByUser(user)
                .ifPresent(sec -> { sec.setAttempts(0); sec.setLockedUntil(null); securityRepository.save(sec);}));
    }

    /**
     * Record a failed login attempt for the user identified by username or email.
     * If the number of failed attempts exceeds the maximum within the time window,
     * lock the account for a specified duration.
     * @param usernameOrEmail username or email
     */
    @Transactional
    public void onFailure(String usernameOrEmail) {
        userRepository.findByUsernameOrEmail(usernameOrEmail)
            .ifPresent(user -> {
                UserLoginSecurity sec = securityRepository.findByUser(user).orElseGet(() -> new UserLoginSecurity(user));
                LocalDateTime now = LocalDateTime.now();
                if (sec.getLockedUntil() != null && now.isBefore(sec.getLockedUntil())) return;
                if (sec.getWindowStart() == null || now.isAfter(sec.getWindowStart().plusMinutes(windowMinutes))) {
                    sec.setWindowStart(now);
                    sec.setAttempts(0);
                }
                sec.setAttempts(sec.getAttempts() + 1);
                if (sec.getAttempts() >= maxAttempts) {
                    sec.setLockedUntil(now.plusMinutes(lockMinutes));
                    sec.setAttempts(0);
                    sec.setWindowStart(now);
                }
                securityRepository.save(sec);
            });
    }
    
    /**
     * Check if user account is locked
     * @param usernameOrEmail username or email
     * @return true if locked, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isLocked(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
            .flatMap(securityRepository::findByUser)
            .map(sec -> {
                LocalDateTime lu = sec.getLockedUntil();
                return lu != null && LocalDateTime.now().isBefore(lu);
            }).orElse(false);
    }
    
    /**
     * Admin unlock user account
     * @param userId user id
     */
    @Transactional
    public void adminUnlock(Long userId) {
        userRepository.findById(userId).ifPresent(user -> securityRepository.findByUser(user)
            .ifPresent(sec -> { sec.setLockedUntil(null); sec.setAttempts(0); securityRepository.save(sec);}));
    }
}