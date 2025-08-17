package com.brainacad.ecs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.security.Role;

/**
 * Student repository interface extending Spring Data JPA repository
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    List<Student> findByCourseId(@Param("courseId") Long courseId);
    
    @Query("SELECT s FROM Student s WHERE s.name = :name OR s.lastName = :name")
    List<Student> findByName(@Param("name") String name);
    
    List<Student> findByAge(Integer age);
    
    @Query("SELECT s FROM Student s WHERE s.role = :role")
    List<Student> findByRole(@Param("role") Role role);
    
    Optional<Student> findByUsername(String username);
}
