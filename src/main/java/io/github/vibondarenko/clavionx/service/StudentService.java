package io.github.vibondarenko.clavionx.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import io.github.vibondarenko.clavionx.dto.student.CreateStudentRequest;
import io.github.vibondarenko.clavionx.dto.student.StudentDetailDto;
import io.github.vibondarenko.clavionx.dto.student.StudentListDto;
import io.github.vibondarenko.clavionx.dto.student.StudentSearchCriteria;
import io.github.vibondarenko.clavionx.dto.student.UpdateStudentRequest;

/**
 * Service interface for managing students
 */
public interface StudentService {
    
    /**
     * Create a new student with generated student number and username if not provided
     * 
     * @param request Student creation data
     * @return Created student details
     */
    StudentDetailDto createStudent(CreateStudentRequest request);
    
    /**
     * Get student by ID
     * 
     * @param id Student ID
     * @return Student details if found
     */
    Optional<StudentDetailDto> getStudent(Long id);
    
    /**
     * Update existing student
     * 
     * @param id Student ID
     * @param request Update data
     * @return Updated student details
     */
    StudentDetailDto updateStudent(Long id, UpdateStudentRequest request);
    
    /**
     * Delete student by ID
     * 
     * @param id Student ID
     */
    void deleteStudent(Long id);
    
    /**
     * Search students with pagination and filtering
     * 
     * @param criteria Search criteria
     * @param pageable Pagination parameters
     * @return Page of student list items
     */
    Page<StudentListDto> searchStudents(StudentSearchCriteria criteria, Pageable pageable);
    
    /**
     * Enroll student in a course
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     */
    void enrollStudentInCourse(Long studentId, Long courseId);
    
    /**
     * Unenroll student from a course
     * 
     * @param studentId Student ID
     * @param courseId Course ID
     */
    void unenrollStudentFromCourse(Long studentId, Long courseId);
    
    /**
     * Get current authenticated student profile
     * 
     * @param authentication Current authentication
     * @return Student profile if authenticated user is a student
     */
    Optional<StudentDetailDto> getCurrentStudentProfile(Authentication authentication);
    
    /**
     * Get total count of students
     * 
     * @return Total number of students
     */
    long getTotalStudentsCount();
    
    /**
     * Get count of active (enabled) students
     * 
     * @return Number of active students
     */
    long getActiveStudentsCount();
    
    /**
     * Check if student number is already taken
     * 
     * @param studentNumber Student number to check
     * @return true if number exists
     */
    boolean isStudentNumberTaken(String studentNumber);
}