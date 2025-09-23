package io.github.vibondarenko.clavionx.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserPasswordHistory;
import io.github.vibondarenko.clavionx.repository.UserPasswordHistoryRepository;

/**
 * Service for managing user password history.
 * It allows validating new passwords against current and historical passwords,
 * storing new password hashes, and purging old password history entries.
 */
@Service
public class PasswordHistoryService {
    private final int historySize;
    private final UserPasswordHistoryRepository repository;
    private final PasswordEncoder passwordEncoder;

    public PasswordHistoryService(UserPasswordHistoryRepository repository,
                                    PasswordEncoder passwordEncoder,
                                    @Value("${security.password-history.size:5}") int historySize) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.historySize = historySize;
    }
    /**
     * Validates that the new raw password is not the same as the user's current password.
     *
     * @param user            The user whose password is being changed.
     * @param newRawPassword  The new raw password to validate.
     * @throws IllegalArgumentException if the new password matches the current password.
     */
    @Transactional(readOnly = true)
    public void validateNotSameAsCurrent(User user, String newRawPassword) {
        if (passwordEncoder.matches(newRawPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must differ from current password");
        }
    }
    /**
     * Validates that the new raw password is not the same as any of the user's recent passwords
     * stored in the password history.
     *
     * @param user            The user whose password is being changed.
     * @param newRawPassword  The new raw password to validate.
     * @throws IllegalArgumentException if the new password matches any of the recent passwords in history.
     */
    @Transactional(readOnly = true)
    public void validateNotInHistory(User user, String newRawPassword) {
        if (historySize <= 0) return;
        List<String> hashes = repository.findHashesByUserOrderByChangedAtDesc(user);
        int limit = Math.min(historySize, hashes.size());
        for (int i = 0; i < limit; i++) {
            if (passwordEncoder.matches(newRawPassword, hashes.get(i))) {
                throw new IllegalArgumentException("New password must not match last " + historySize + " passwords");
            }
        }
    }
    /**
     * Stores the encoded password hash in the user's password history and purges excess entries
     * if the history exceeds the configured size.
     *
     * @param user            The user whose password is being stored.
     * @param encodedPassword The encoded password hash to store.
     */
    @Transactional
    public void storePasswordHash(User user, String encodedPassword) {
        repository.save(new UserPasswordHistory(user, encodedPassword));
        purgeExcess(user);
    }
    /**
     * Purges excess password history entries for the user if the number of entries exceeds
     * the configured history size.
     *
     * @param user The user whose password history is to be purged.
     */
    void purgeExcess(User user) {
        if (historySize <= 0) return;
        List<UserPasswordHistory> all = repository.findTop10ByUserOrderByChangedAtDesc(user);
        if (all.size() <= historySize) return;
        List<UserPasswordHistory> toDelete = all.stream().skip(historySize).toList();
        repository.deleteAll(toDelete);
    }
}