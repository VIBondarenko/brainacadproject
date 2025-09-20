package io.github.vibondarenko.clavionx.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.UserLoginSecurity;
import io.github.vibondarenko.clavionx.repository.UserLoginSecurityRepository;
import io.github.vibondarenko.clavionx.repository.UserRepository;

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

    @Transactional
    public void onSuccess(String usernameOrEmail) {
        userRepository.findByUsernameOrEmail(usernameOrEmail)
            .ifPresent(user -> securityRepository.findByUser(user)
                .ifPresent(sec -> { sec.setAttempts(0); sec.setLockedUntil(null); securityRepository.save(sec);}));
    }

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

    @Transactional(readOnly = true)
    public boolean isLocked(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
            .flatMap(securityRepository::findByUser)
            .map(sec -> {
                LocalDateTime lu = sec.getLockedUntil();
                return lu != null && LocalDateTime.now().isBefore(lu);
            }).orElse(false);
    }

    @Transactional
    public void adminUnlock(Long userId) {
        userRepository.findById(userId).ifPresent(user -> securityRepository.findByUser(user)
            .ifPresent(sec -> { sec.setLockedUntil(null); sec.setAttempts(0); securityRepository.save(sec);}));
    }
}
