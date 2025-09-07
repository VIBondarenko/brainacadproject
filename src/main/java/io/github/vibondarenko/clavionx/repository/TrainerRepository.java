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
 */
@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    
    @Query("SELECT t FROM Trainer t JOIN t.courses c WHERE c.id = :courseId")
    List<Trainer> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT t FROM Trainer t WHERE t.name = :name OR t.lastName = :name")
    List<Trainer> findByName(@Param("name") String name);
    
    @Query("SELECT t FROM Trainer t WHERE t NOT IN (SELECT c.trainer FROM Course c WHERE c.id = :courseId)")
    List<Trainer> findFreeByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT t FROM Trainer t WHERE t.role = :role")
    List<Trainer> findByRole(@Param("role") Role role);
    
    Optional<Trainer> findByUsername(String username);
}



