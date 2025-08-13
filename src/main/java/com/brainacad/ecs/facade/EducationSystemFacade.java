package com.brainacad.ecs.facade;

import com.brainacad.ecs.service.CourseService;
import com.brainacad.ecs.service.StudentService;
import com.brainacad.ecs.service.PersistenceService;
import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.repository.TrainerRepository;
import com.brainacad.ecs.repository.TaskRepository;
import com.brainacad.ecs.repository.impl.*;
import com.brainacad.ecs.service.impl.*;
import com.brainacad.ecs.Course;
import com.brainacad.ecs.Student;
import com.brainacad.ecs.Trainer;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Education System Facade
 * Implements Facade Pattern to provide a simplified interface to the complex subsystem
 * Follows Single Responsibility Principle - serves as the main entry point
 * Supports Open/Closed Principle - new services can be added without modifying existing code
 */
public class EducationSystemFacade {
    private static final Logger logger = Logger.getLogger(EducationSystemFacade.class.getName());
    private static EducationSystemFacade instance;
    
    // Repositories
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;
    private final TaskRepository taskRepository;
    
    // Services
    private final CourseService courseService;
    private final StudentService studentService;
    private final PersistenceService persistenceService;

    private EducationSystemFacade() {
        // Initialize repositories
        courseRepository = new InMemoryCourseRepository();
        studentRepository = new InMemoryStudentRepository();
        trainerRepository = new InMemoryTrainerRepository();
        taskRepository = new InMemoryTaskRepository();
        
        // Initialize services
        courseService = new CourseServiceImpl(courseRepository, studentRepository, trainerRepository);
        studentService = new StudentServiceImpl(studentRepository, courseRepository, taskRepository);
        persistenceService = new FilePersistenceService(courseRepository, studentRepository, trainerRepository, taskRepository);
        
        logger.log(Level.INFO, "Education System initialized with new SOLID architecture");
    }
    
    /**
     * Singleton pattern implementation
     * @return The single instance of EducationSystemFacade
     */
    public static synchronized EducationSystemFacade getInstance() {
        if (instance == null) {
            instance = new EducationSystemFacade();
            
            // Try to load existing data
            try {
                instance.loadData();
            } catch (IOException | ClassNotFoundException e) {
                logger.log(Level.INFO, "No existing data found, starting with empty system");
            }
        }
        return instance;
    }
    
    // Course Management Operations
    public void createCourse(String name, String startDate, String finishDate, int countPlaces) {
        courseService.createCourse(name, startDate, finishDate, countPlaces);
    }
    
    public Optional<Course> getCourse(int id) {
        return courseService.getCourse(id);
    }
    
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }
    
    public List<Course> getFreeCourses() {
        return courseService.getFreeCourses();
    }
    
    public void deleteCourse(int id) {
        courseService.deleteCourse(id);
    }
    
    // Additional Course Operations for Web Interface
    public Course findCourseById(int id) {
        Optional<Course> course = getCourse(id);
        return course.orElse(null);
    }
    
    public void addCourse(Course course) {
        String startDate = course.getBeginDate() != null ? course.getBeginDate().toString() : "";
        String finishDate = course.getEndDate() != null ? course.getEndDate().toString() : "";
        courseService.createCourse(course.getName(), startDate, finishDate, course.getCountPlaces());
    }
    
    public void removeCourse(Course course) {
        courseService.deleteCourse(course.getId());
    }
    
    public boolean enrollStudentInCourse(int courseId, int studentId) {
        return courseService.enrollStudent(courseId, studentId);
    }
    
    public boolean removeStudentFromCourse(int courseId, int studentId) {
        return courseService.removeStudent(courseId, studentId);
    }
    
    public boolean assignTrainerToCourse(int courseId, int trainerId) {
        return courseService.assignTrainer(courseId, trainerId);
    }
    
    // Additional methods for Trainer and Student operations
    public Trainer findTrainerById(int id) {
        List<Trainer> trainers = getAllTrainers();
        return trainers.stream()
                .filter(trainer -> trainer.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public Student findStudentById(int id) {
        List<Student> students = getAllStudents();
        return students.stream()
                .filter(student -> student.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    public List<Student> getStudentsInCourse(int courseId) {
        return courseService.getStudentsInCourse(courseId);
    }
    
    // Student Management Operations
    public void createStudent(String firstName, String lastName) {
        studentService.createStudent(firstName, lastName, 0); // Age not implemented yet
    }
    
    public Optional<Student> getStudent(int id) {
        return studentService.getStudent(id);
    }
    
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    public void deleteStudent(int id) {
        studentService.deleteStudent(id);
    }
    
    public List<Course> getStudentCourses(int studentId) {
        return studentService.getStudentCourses(studentId);
    }
    
    // Trainer Management Operations  
    public void createTrainer(String firstName, String lastName) {
        Trainer trainer = new Trainer(firstName, lastName);
        trainerRepository.save(trainer);
    }
    
    public Optional<Trainer> getTrainer(int id) {
        return trainerRepository.findById(id);
    }
    
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }
    
    public void deleteTrainer(int id) {
        Optional<Trainer> trainer = trainerRepository.findById(id);
        if (trainer.isPresent()) {
            trainer.get().deleteTrainerFromCourses();
            trainerRepository.deleteById(id);
        }
    }
    
    // Data Persistence Operations
    public void saveData() {
        try {
            persistenceService.saveData();
            logger.log(Level.INFO, "System data saved successfully");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save system data: {0}", e.getMessage());
        }
    }
    
    public void loadData() throws IOException, ClassNotFoundException {
        persistenceService.loadData();
        logger.log(Level.INFO, "System data loaded successfully");
    }
    
    // Search Operations (demonstrating Open/Closed Principle)
    public Optional<Course> findCourseByName(String name) {
        return courseRepository.findByName(name);
    }
    
    public Optional<Student> findStudentByName(String name) {
        return studentRepository.findByName(name);
    }
    
    public Optional<Trainer> findTrainerByName(String name) {
        return trainerRepository.findByName(name);
    }
    
    // Statistics Operations
    public int getTotalCoursesCount() {
        return (int) courseRepository.count();
    }
    
    public int getTotalStudentsCount() {
        return (int) studentRepository.count();
    }
    
    public int getTotalTrainersCount() {
        return (int) trainerRepository.count();
    }
    
    public int getAvailablePlacesInCourse(int courseId) {
        return courseService.getAvailablePlaces(courseId);
    }
    
    public int getEnrolledStudentsInCourse(int courseId) {
        return courseService.getEnrolledStudentsCount(courseId);
    }
}
