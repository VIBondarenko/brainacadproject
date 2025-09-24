package io.github.vibondarenko.clavionx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.Trainer;
import io.github.vibondarenko.clavionx.security.Role;

/**
 * Trainer repository interface extending Spring Data JPA repository
 * for CRUD operations and custom queries on Trainer entities.
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    /**
     * Find trainers associated with a specific course ID.
     *
     * @param courseId the ID of the course
     * @return a list of trainers associated with the given course ID
     */
    @Query("SELECT t FROM Trainer t JOIN t.courses c WHERE c.id = :courseId")
    List<Trainer> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Find trainers by name (first or last).
     *
     * @param name the name to search for
     * @return a list of trainers matching the given name
     */
    @Query("SELECT t FROM Trainer t WHERE t.name = :name OR t.lastName = :name")
    List<Trainer> findByName(@Param("name") String name);

    /**
     * Find trainers who are not assigned to a specific course.
     *
     * @param courseId the ID of the course
     * @return a list of trainers not associated with the given course ID
     */
    @Query("SELECT t FROM Trainer t WHERE t NOT IN (SELECT c.trainer FROM Course c WHERE c.id = :courseId)")
    List<Trainer> findFreeByCourseId(@Param("courseId") Long courseId);
    /**
     * Find trainers by their role.
     *
     * @param role the role to search for
     * @return a list of trainers with the specified role
     */
    @Query("SELECT t FROM Trainer t WHERE t.role = :role")
    List<Trainer> findByRole(@Param("role") Role role);
    /**
     * Find a trainer by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the trainer if found, or empty if not found
     */
    Optional<Trainer> findByUsername(String username);
}



