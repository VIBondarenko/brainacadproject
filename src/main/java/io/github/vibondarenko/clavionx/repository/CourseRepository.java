package io.github.vibondarenko.clavionx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.Course;

/**
 * Course repository interface extending Spring Data JPA repository
 * for managing Course entities.
 * Provides methods to perform CRUD operations and custom queries.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    /**
     * Find courses by trainer ID.
     * @param trainerId
     * @return List of courses associated with the trainer
     */
    @Query("SELECT c FROM Course c WHERE c.trainer.id = :trainerId")
    List<Course> findByTrainerId(@Param("trainerId") Long trainerId);
    /**
     * Find courses by student ID.
     * @param studentId
     * @return List of courses associated with the student
     */
    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);
    /**
     * Find courses without assigned trainers.
     * @return List of courses without trainers
     */
    @Query("SELECT c FROM Course c WHERE c.trainer IS NULL")
    List<Course> findFreeCourses();
    /**
     * Find course by its name.
     * @param name
     * @return Optional containing the course if found, otherwise empty
     */
    Optional<Course> findByName(String name);
    /**
     * Find courses within a specific date range.
     * @param startDate
     * @param endDate
     * @return List of courses within the date range
     */
    @Query("SELECT c FROM Course c WHERE c.beginDate >= :startDate AND c.endDate <= :endDate")
    List<Course> findByDateRange(@Param("startDate") java.time.LocalDate startDate, 
                                @Param("endDate") java.time.LocalDate endDate);
}



