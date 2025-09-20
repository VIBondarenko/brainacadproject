package io.github.vibondarenko.clavionx.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Tracks login attempts and temporary lockouts (in-memory).
 * For clustered/production use, back with DB/Redis.
 */
@Service
public class LoginAttemptService {
    private final int maxAttempts;
    private final int windowMinutes;
    private final int lockMinutes;

    private static class Stats {
        int attempts;
        Instant windowStart;
        Instant lockedUntil;
    }

    private final Map<String, Stats> store = new ConcurrentHashMap<>();

    public LoginAttemptService(
            @Value("${security.lockout.max-attempts:5}") int maxAttempts,
            @Value("${security.lockout.window-minutes:15}") int windowMinutes,
            @Value("${security.lockout.lock-minutes:15}") int lockMinutes) {
        this.maxAttempts = maxAttempts;
        this.windowMinutes = windowMinutes;
        this.lockMinutes = lockMinutes;
    }

    public void onSuccess(String key) {
        store.remove(key);
    }

    public void onFailure(String key) {
        Stats s = store.computeIfAbsent(key, k -> {
            Stats ns = new Stats();
            ns.attempts = 0;
            ns.windowStart = Instant.now();
            return ns;
        });
        Instant now = Instant.now();
        if (s.lockedUntil != null && now.isBefore(s.lockedUntil)) {
            return; // already locked
        }
        if (now.isAfter(s.windowStart.plus(windowMinutes, ChronoUnit.MINUTES))) {
            s.attempts = 0;
            s.windowStart = now;
        }
        s.attempts++;
        if (s.attempts >= maxAttempts) {
            s.lockedUntil = now.plus(lockMinutes, ChronoUnit.MINUTES);
            s.attempts = 0;
            s.windowStart = now;
        }
        store.put(key, s);
    }

    public boolean isLocked(String key) {
        Stats s = store.get(key);
        if (s == null || s.lockedUntil == null) return false;
        if (Instant.now().isAfter(s.lockedUntil)) {
            s.lockedUntil = null;
            return false;
        }
        return true;
    }

    public Instant lockedUntil(String key) {
        Stats s = store.get(key);
        return s != null ? s.lockedUntil : null;
    }
}
