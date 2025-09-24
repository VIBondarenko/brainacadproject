package io.github.vibondarenko.clavionx.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserLoginSecurity;

/**
 * Repository interface for managing UserLoginSecurity entities.
 * Provides methods to perform CRUD operations and custom queries.
 * Extends JpaRepository to leverage Spring Data JPA functionalities.
 * @see UserLoginSecurity
 * @see JpaRepository
 * @see User
 */
public interface UserLoginSecurityRepository extends JpaRepository<UserLoginSecurity, Long> {
    
    /**
     * Find login security information by user.
     * @param user the user entity
     * @return an Optional containing the UserLoginSecurity entity if found, or empty if not
     */
    Optional<UserLoginSecurity> findByUser(User user);
}