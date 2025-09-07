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
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    @Query("SELECT c FROM Course c WHERE c.trainer.id = :trainerId")
    List<Course> findByTrainerId(@Param("trainerId") Long trainerId);
    
    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Course c WHERE c.trainer IS NULL")
    List<Course> findFreeCourses();
    
    Optional<Course> findByName(String name);
    
    @Query("SELECT c FROM Course c WHERE c.beginDate >= :startDate AND c.endDate <= :endDate")
    List<Course> findByDateRange(@Param("startDate") java.time.LocalDate startDate, 
                                @Param("endDate") java.time.LocalDate endDate);
}



