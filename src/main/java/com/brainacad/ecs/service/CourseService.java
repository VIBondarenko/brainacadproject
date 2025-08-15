package com.brainacad.ecs.service;

import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.entity.Trainer;
import com.brainacad.ecs.entity.Course;

import java.util.List;
import java.util.Optional;

/**
 * Course service interface for business logic operations
 */
public interface CourseService {
    void createCourse(String name, String startDate, String finishDate, int countPlaces);
    Optional<Course> getCourse(int id);
    List<Course> getAllCourses();
    List<Course> getFreeCourses();
    void updateCourse(Course course);
    void deleteCourse(int id);
    
    // Student enrollment operations
    boolean enrollStudent(int courseId, int studentId);
    boolean removeStudent(int courseId, int studentId);
    List<Student> getStudentsInCourse(int courseId);
    
    // Trainer assignment operations
    boolean assignTrainer(int courseId, int trainerId);
    boolean removeTrainer(int courseId);
    Optional<Trainer> getCourseTrainer(int courseId);
    
    // Course statistics
    int getAvailablePlaces(int courseId);
    int getEnrolledStudentsCount(int courseId);
}
