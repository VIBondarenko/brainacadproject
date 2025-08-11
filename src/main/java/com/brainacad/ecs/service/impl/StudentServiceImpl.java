package com.brainacad.ecs.service.impl;

import com.brainacad.ecs.service.StudentService;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.repository.TaskRepository;
import com.brainacad.ecs.Student;
import com.brainacad.ecs.Course;
import com.brainacad.ecs.Task;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Student service implementation following business logic patterns
 * Follows Single Responsibility Principle - only handles Student business logic
 */
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = Logger.getLogger(StudentServiceImpl.class.getName());
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    public StudentServiceImpl(StudentRepository studentRepository, 
                            CourseRepository courseRepository,
                            TaskRepository taskRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void createStudent(String firstName, String lastName, int age) {
        if (firstName == null || firstName.trim().isEmpty() || 
            lastName == null || lastName.trim().isEmpty()) {
            logger.log(Level.WARNING, "Cannot create student with empty name");
            return;
        }
        
        Student student = new Student(firstName, lastName);
        studentRepository.save(student);
        logger.log(Level.INFO, "Student created: {0} {1}", new Object[]{firstName, lastName});
    }

    @Override
    public Optional<Student> getStudent(int id) {
        return studentRepository.findById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public void updateStudent(Student student) {
        if (student != null) {
            studentRepository.update(student);
        }
    }

    @Override
    public void deleteStudent(int id) {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            Student studentToDelete = student.get();
            
            // Remove student from all enrolled courses
            List<Course> enrolledCourses = studentToDelete.getCourses();
            for (Course course : enrolledCourses) {
                course.deleteStudent(studentToDelete);
                courseRepository.update(course);
            }
            
            // Clean up student's course relationships
            studentToDelete.deleteStudentFromCourses();
            
            studentRepository.deleteById(id);
            logger.log(Level.INFO, "Student deleted with ID: {0}", id);
        }
    }

    @Override
    public List<Course> getStudentCourses(int studentId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()) {
            return student.get().getCourses();
        }
        return List.of();
    }

    @Override
    public boolean enrollInCourse(int studentId, int courseId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (!studentOpt.isPresent() || !courseOpt.isPresent()) {
            logger.log(Level.WARNING, "Student or Course not found for enrollment");
            return false;
        }
        
        Student student = studentOpt.get();
        Course course = courseOpt.get();
        
        if (course.getCountPlaces() <= 0) {
            logger.log(Level.WARNING, "No available places in course: {0}", course.getName());
            return false;
        }
        
        if (course.addStudent(student)) {
            student.addCourse(course);
            courseRepository.update(course);
            studentRepository.update(student);
            logger.log(Level.INFO, "Student {0} enrolled in course {1}", 
                      new Object[]{student.getName(), course.getName()});
            return true;
        }
        
        return false;
    }

    @Override
    public boolean withdrawFromCourse(int studentId, int courseId) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (!studentOpt.isPresent() || !courseOpt.isPresent()) {
            logger.log(Level.WARNING, "Student or Course not found for withdrawal");
            return false;
        }
        
        Student student = studentOpt.get();
        Course course = courseOpt.get();
        
        course.deleteStudent(student);
        student.deleteCourse(course);
        
        courseRepository.update(course);
        studentRepository.update(student);
        
        logger.log(Level.INFO, "Student {0} withdrawn from course {1}", 
                  new Object[]{student.getName(), course.getName()});
        return true;
    }

    @Override
    public List<Task> getStudentTasks(int studentId) {
        // Implementation would depend on how task-student relationships are managed
        // For now, return tasks from all student's courses
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()) {
            List<Course> courses = student.get().getCourses();
            return courses.stream()
                    .flatMap(course -> taskRepository.findByCourseId(course.getId()).stream())
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        }
        return List.of();
    }

    @Override
    public List<Task> getStudentTasksForCourse(int studentId, int courseId) {
        Optional<Student> student = studentRepository.findById(studentId);
        if (student.isPresent()) {
            return student.get().getTasks(courseId);
        }
        return List.of();
    }

    @Override
    public int getCompletedTasksCount(int studentId) {
        // This would require task completion status tracking
        // For now, return 0
        logger.log(Level.INFO, "getCompletedTasksCount not fully implemented - requires task completion tracking");
        return 0;
    }

    @Override
    public double getAverageGrade(int studentId) {
        // This would require grade tracking in the journal
        // For now, return 0.0
        logger.log(Level.INFO, "getAverageGrade not fully implemented - requires grade calculation from journal");
        return 0.0;
    }
}
