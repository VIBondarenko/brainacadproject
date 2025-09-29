package io.github.vibondarenko.clavionx.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.vibondarenko.clavionx.dto.student.CreateStudentRequest;
import io.github.vibondarenko.clavionx.dto.student.CourseBriefDto;
import io.github.vibondarenko.clavionx.dto.student.StudentDetailDto;
import io.github.vibondarenko.clavionx.dto.student.StudentListDto;
import io.github.vibondarenko.clavionx.dto.student.StudentSearchCriteria;
import io.github.vibondarenko.clavionx.dto.student.UpdateStudentRequest;
import io.github.vibondarenko.clavionx.entity.Course;
import io.github.vibondarenko.clavionx.entity.Student;
import io.github.vibondarenko.clavionx.repository.CourseRepository;
import io.github.vibondarenko.clavionx.repository.StudentRepository;
import io.github.vibondarenko.clavionx.security.Role;
import io.github.vibondarenko.clavionx.service.StudentService;

/**
 * Implementation of StudentService
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {
    
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private static final String DEFAULT_PASSWORD = "TempPass123!"; // Should be changed on first login
    
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;
    
    public StudentServiceImpl(StudentRepository studentRepository, 
                            CourseRepository courseRepository,
                            PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public StudentDetailDto createStudent(CreateStudentRequest request) {
        logger.info("Creating new student with email: {}", request.email());
        
        // Generate username if not provided
        String username = request.username() != null ? request.username() 
                            : generateUsername(request.name(), request.lastName());
        
        // Generate student number
        String studentNumber = generateStudentNumber();
        
        // Create student entity
        LocalDate graduationDate = null;
        if (request.graduationYear() != null) {
            // Graduation date default to June 30 of the graduation year
            graduationDate = LocalDate.of(request.graduationYear(), 6, 30);
        }
        Student student = new Student(
                request.name(),
                request.lastName(),
                username,
                passwordEncoder.encode(DEFAULT_PASSWORD),
                request.email(),
                studentNumber,
                graduationDate
        );
        
        if (request.phoneNumber() != null) {
            student.setPhoneNumber(request.phoneNumber());
        }
        
        student.setEnrollmentDate(LocalDate.now());
        student.setRole(Role.STUDENT);
        student.setEnabled(true);
        
        Student savedStudent = studentRepository.save(student);
        logger.info("Created student with ID: {} and student number: {}", 
                    savedStudent.getId(), savedStudent.getStudentNumber());
        
        return StudentDetailDto.from(savedStudent);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDetailDto> getStudent(Long id) {
        return studentRepository.findById(id)
                .map(StudentDetailDto::from);
    }
    
    @Override
    public StudentDetailDto updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
        
        logger.info("Updating student with ID: {}", id);
        
        if (request.name() != null) {
            student.setName(request.name());
        }
        if (request.lastName() != null) {
            student.setLastName(request.lastName());
        }
        if (request.email() != null) {
            student.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            student.setPhoneNumber(request.phoneNumber());
        }
        if (request.graduationYear() != null) {
            student.setGraduationDate(LocalDate.of(request.graduationYear(), 6, 30));
        }
        
        Student savedStudent = studentRepository.save(student);
        return StudentDetailDto.from(savedStudent);
    }
    
    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
        
        logger.info("Deleting student with ID: {} and student number: {}", 
                    id, student.getStudentNumber());
        
        studentRepository.delete(student);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<StudentListDto> searchStudents(StudentSearchCriteria criteria, Pageable pageable) {
        if (criteria.hasQuery()) {
            return studentRepository.findByQueryString(criteria.query(), pageable)
                    .map(StudentListDto::from);
        }
        
        // If no criteria, return all students
        return studentRepository.findAll(pageable)
                .map(StudentListDto::from);
    }
    
    @Override
    public void enrollStudentInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        
        if (student.isEnrolledInCourse(courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }
        
        logger.info("Enrolling student {} in course {}", studentId, courseId);
        
        // Add to both sides of the relationship
        student.addCourse(course);
        course.addStudent(student);
        
        studentRepository.save(student);
        courseRepository.save(course);
    }
    
    @Override
    public void unenrollStudentFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));
        
        if (!student.isEnrolledInCourse(courseId)) {
            throw new IllegalStateException("Student is not enrolled in this course");
        }
        
        logger.info("Unenrolling student {} from course {}", studentId, courseId);
        
        // Remove from both sides of the relationship
        student.removeCourse(course);
        course.removeStudent(student);
        
        // Remove tasks associated with this course
        student.removeTasksFromCourse(courseId);
        
        studentRepository.save(student);
        courseRepository.save(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDetailDto> getCurrentStudentProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        
        String username = authentication.getName();
        return studentRepository.findByUsername(username)
                .map(StudentDetailDto::from);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalStudentsCount() {
        return studentRepository.count();
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getActiveStudentsCount() {
        return studentRepository.countByEnabled(true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isStudentNumberTaken(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseBriefDto> getEnrolledCourses(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
        return student.getCourses().stream().map(CourseBriefDto::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseBriefDto> getAvailableCourses(Long studentId) {
        return courseRepository.findCoursesNotEnrolledByStudent(studentId)
                .stream().map(CourseBriefDto::from).toList();
    }
    
    /**
     * Generate username from name and lastName
     */
    private String generateUsername(String name, String lastName) {
        String baseUsername = (name.toLowerCase() + "." + lastName.toLowerCase())
                .replaceAll("[^a-z.]", "");
        
        String username = baseUsername;
        int counter = 1;
        
        while (isUsernameTaken(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
    
    /**
     * Check if username is already taken
     */
    private boolean isUsernameTaken(String username) {
        return studentRepository.findByUsername(username).isPresent();
    }
    
    /**
     * Generate unique student number in format STU-YYYY-####
     */
    private synchronized String generateStudentNumber() {
        int currentYear = LocalDate.now().getYear();
        String prefix = "STU-" + currentYear + "-";
        
        int counter = 1;
        String studentNumber;
        
        do {
            studentNumber = prefix + String.format("%04d", counter);
            counter++;
        } while (isStudentNumberTaken(studentNumber));
        
        return studentNumber;
    }
}