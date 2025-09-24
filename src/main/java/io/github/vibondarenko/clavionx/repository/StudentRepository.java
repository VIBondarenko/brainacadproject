package io.github.vibondarenko.clavionx.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.github.vibondarenko.clavionx.entity.Student;
import io.github.vibondarenko.clavionx.security.Role;

/**
 * Student repository interface extending Spring Data JPA repository
 * for CRUD operations and custom queries on Student entities.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    /**
     * Finds students enrolled in a specific course by course ID.
     *
     * @param courseId the ID of the course
     * @return a list of students enrolled in the specified course
     */
    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.id = :courseId")
    List<Student> findByCourseId(@Param("courseId") Long courseId);
    /**
     * Finds students by their name or last name.
     *
     * @param name the name or last name to search for
     * @return a list of students matching the given name or last name
     */
    @Query("SELECT s FROM Student s WHERE s.name = :name OR s.lastName = :name")
    List<Student> findByName(@Param("name") String name);
    /**
     * Finds students by their age.
     *
     * @param age the age to search for
     * @return a list of students matching the given age
     */
    List<Student> findByAge(Integer age);
    /**
     * Finds students by their role.
     *
     * @param role the role to search for
     * @return a list of students matching the given role
     */
    @Query("SELECT s FROM Student s WHERE s.role = :role")
    List<Student> findByRole(@Param("role") Role role);
    /**
     * Finds a student by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the student if found, or empty if not found
     */
    Optional<Student> findByUsername(String username);
}



