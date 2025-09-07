package io.github.vibondarenko.clavionx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.Task;

/**
 * Task repository interface extending Spring Data JPA repository
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByCourseId(Long courseId);
    
    @Query("SELECT t FROM Task t JOIN t.course c JOIN c.students s WHERE s.id = :studentId")
    List<Task> findByStudentId(@Param("studentId") Long studentId);
    
    Optional<Task> findByName(String name);
}



