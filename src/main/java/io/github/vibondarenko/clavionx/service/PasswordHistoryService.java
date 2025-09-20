package io.github.vibondarenko.clavionx.service;

import java.util.List;
// no extra imports

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserPasswordHistory;
import io.github.vibondarenko.clavionx.repository.UserPasswordHistoryRepository;

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

    @Transactional(readOnly = true)
    public void validateNotSameAsCurrent(User user, String newRawPassword) {
        if (passwordEncoder.matches(newRawPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must differ from current password");
        }
    }

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

    @Transactional
    public void storePasswordHash(User user, String encodedPassword) {
        repository.save(new UserPasswordHistory(user, encodedPassword));
        purgeExcess(user);
    }

    void purgeExcess(User user) {
        if (historySize <= 0) return;
        List<UserPasswordHistory> all = repository.findTop10ByUserOrderByChangedAtDesc(user);
        if (all.size() <= historySize) return;
        List<UserPasswordHistory> toDelete = all.stream().skip(historySize).toList();
        repository.deleteAll(toDelete);
    }
}
