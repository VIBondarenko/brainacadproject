package com.brainacad.ecs.adapter;

import com.brainacad.ecs.facade.EducationSystemFacade;
import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.entity.Trainer;
import com.brainacad.ecs.entity.Course;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Adapter for the legacy Storage class
 * Implements Adapter Pattern to provide backward compatibility
 * Delegates to the new SOLID-compliant EducationSystemFacade
 * 
 * Note: File-based persistence methods (read/write) are now legacy stubs
 * as the system uses H2 database with JPA for automatic persistence
 */
public class StorageAdapter {
    private static final Logger logger = Logger.getLogger(StorageAdapter.class.getName());
    private static StorageAdapter instance;
    private final EducationSystemFacade educationSystem;

    private StorageAdapter() {
        educationSystem = EducationSystemFacade.getInstance();
        logger.log(Level.INFO, "Storage adapter initialized - bridging legacy API with new SOLID architecture");
    }

    public static StorageAdapter getInstance() {
        if (instance == null) {
            instance = new StorageAdapter();
        }
        return instance;
    }

    // Legacy Course operations
    public void addCourse(Course course) {
        if (course != null) {
            educationSystem.createCourse(course.getName(), 
                                       formatDate(course.getBeginDate()), 
                                       formatDate(course.getEndDate()), 
                                       course.getCountPlaces());
        }
    }

    public Course searchCourseById(int id) {
        Optional<Course> course = educationSystem.getCourse(id);
        return course.orElse(null);
    }

    public List<Course> getCourses() {
        return educationSystem.getAllCourses();
    }

    public void deleteCourse(int id) {
        educationSystem.deleteCourse(id);
    }

    public List<Course> getFreeCourses() {
        return educationSystem.getFreeCourses();
    }

    public void addStudent(Student student) {
        if (student != null) {
            educationSystem.createStudent(student.getName(), student.getLastName());
        }
    }

    public Student searchStudentById(int id) {
        Optional<Student> student = educationSystem.getStudent(id);
        return student.orElse(null);
    }

    public List<Student> getStudents() {
        return educationSystem.getAllStudents();
    }

    public void deleteStudent(int id) {
        educationSystem.deleteStudent(id);
    }

    public void addTrainer(Trainer trainer) {
        if (trainer != null) {
            educationSystem.createTrainer(trainer.getName(), trainer.getLastName());
        }
    }

    public Trainer searchTrainerById(int id) {
        Optional<Trainer> trainer = educationSystem.getTrainer(id);
        return trainer.orElse(null);
    }

    public List<Trainer> getTrainers() {
        return educationSystem.getAllTrainers();
    }

    public void deleteTrainer(int id) {
        educationSystem.deleteTrainer(id);
    }

    // Legacy enrollment operations
    public boolean addStudentToCourse(int courseId, int studentId) {
        return educationSystem.enrollStudentInCourse(courseId, studentId);
    }

    public boolean removeStudentFromCourse(int courseId, int studentId) {
        return educationSystem.removeStudentFromCourse(courseId, studentId);
    }

    public boolean assignTrainerToCourse(int courseId, int trainerId) {
        return educationSystem.assignTrainerToCourse(courseId, trainerId);
    }

    public Course findCourseByName(String name) {
        Optional<Course> course = educationSystem.findCourseByName(name);
        return course.orElse(null);
    }

    public Student findStudentByName(String name) {
        Optional<Student> student = educationSystem.findStudentByName(name);
        return student.orElse(null);
    }

    public Trainer findTrainerByName(String name) {
        Optional<Trainer> trainer = educationSystem.findTrainerByName(name);
        return trainer.orElse(null);
    }

    public void write(String fileName) {
        // Legacy method - no longer needed with H2 database
        // Data is automatically persisted via JPA
        logger.log(Level.INFO, "Legacy write() called - data automatically persisted via H2 database");
    }

    public StorageAdapter read(String fileName) throws IOException, ClassNotFoundException {
        // Legacy method - no longer needed with H2 database
        // Data is automatically loaded via JPA on application startup
        logger.log(Level.INFO, "Legacy read() called - data automatically loaded via H2 database");
        return this;
    }

    public int getCoursesCount() {
        return educationSystem.getTotalCoursesCount();
    }

    public int getStudentsCount() {
        return educationSystem.getTotalStudentsCount();
    }

    public int getTrainersCount() {
        return educationSystem.getTotalTrainersCount();
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(date);
    }

}
