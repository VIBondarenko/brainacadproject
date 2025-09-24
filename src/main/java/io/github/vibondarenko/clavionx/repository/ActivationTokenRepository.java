package io.github.vibondarenko.clavionx.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.ActivationToken;

/**
 * Repository interface for managing ActivationToken entities.
 * Provides methods to find and delete activation tokens.
 * Extends JpaRepository to leverage Spring Data JPA functionalities.
 */
@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {
    /**
     * Finds an ActivationToken by its token string.
     *
     * @param token the token string to search for
     * @return an Optional containing the found ActivationToken, or empty if not found
     */
    Optional<ActivationToken> findByToken(String token);
    /**
     * Deletes an ActivationToken associated with the specified user ID.
     *
     * @param userId the ID of the user whose activation token should be deleted
     */
    void deleteByUserId(Long userId);
}