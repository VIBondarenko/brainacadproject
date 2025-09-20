package io.github.vibondarenko.clavionx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserPasswordHistory;

public interface UserPasswordHistoryRepository extends JpaRepository<UserPasswordHistory, Long> {
    List<UserPasswordHistory> findTop10ByUserOrderByChangedAtDesc(User user);

    @Query("SELECT h.passwordHash FROM UserPasswordHistory h WHERE h.user = :user ORDER BY h.changedAt DESC")
    List<String> findHashesByUserOrderByChangedAtDesc(User user);
}
