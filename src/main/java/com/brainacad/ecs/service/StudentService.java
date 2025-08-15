package com.brainacad.ecs.service;

import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.entity.Task;
import com.brainacad.ecs.entity.Course;

import java.util.List;
import java.util.Optional;

/**
 * Student service interface for business logic operations
 */
public interface StudentService {
    void createStudent(String firstName, String lastName, int age);
    Optional<Student> getStudent(int id);
    List<Student> getAllStudents();
    void updateStudent(Student student);
    void deleteStudent(int id);
    
    // Course enrollment operations
    List<Course> getStudentCourses(int studentId);
    boolean enrollInCourse(int studentId, int courseId);
    boolean withdrawFromCourse(int studentId, int courseId);
    
    // Task operations
    List<Task> getStudentTasks(int studentId);
    List<Task> getStudentTasksForCourse(int studentId, int courseId);
    
    // Student statistics
    int getCompletedTasksCount(int studentId);
    double getAverageGrade(int studentId);
}
