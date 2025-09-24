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
 * for CRUD operations and custom queries on Task entities.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Finds tasks by the associated course ID.
     *
     * @param courseId the ID of the course
     * @return a list of tasks associated with the specified course ID
     */
    List<Task> findByCourseId(Long courseId);
    
    /**
     * Finds tasks by the associated student ID through courses.
     *
     * @param studentId the ID of the student
     * @return a list of tasks associated with the specified student ID
     */
    @Query("SELECT t FROM Task t JOIN t.course c JOIN c.students s WHERE s.id = :studentId")
    List<Task> findByStudentId(@Param("studentId") Long studentId);
    
    /**
     * Finds a task by its name.
     *
     * @param name the name of the task
     * @return an Optional containing the task if found, or empty if not found
     */
    Optional<Task> findByName(String name);
}



