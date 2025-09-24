package io.github.vibondarenko.clavionx.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserPasswordHistory;

/**
 * Repository interface for managing user password history.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 * @see UserPasswordHistory
 * @see User
 */
public interface UserPasswordHistoryRepository extends JpaRepository<UserPasswordHistory, Long> {

    /**
     * Find the most recent password changes for a user.
     * @param user the user entity
     * @return a list of the user's password history entries, ordered by change date
     */
    List<UserPasswordHistory> findTop10ByUserOrderByChangedAtDesc(User user);

    /**
     * Retrieve password hashes for a user, ordered by change date descending.
     * @param user the user entity
     * @return a list of password hashes
     */
    @Query("SELECT h.passwordHash FROM UserPasswordHistory h WHERE h.user = :user ORDER BY h.changedAt DESC")
    List<String> findHashesByUserOrderByChangedAtDesc(User user);
}