package com.brainacad.ecs.service.impl;

import com.brainacad.ecs.service.CourseService;
import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.repository.TrainerRepository;
import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.entity.Trainer;
import com.brainacad.ecs.entity.Course;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Course service implementation following business logic patterns
 * Follows Single Responsibility Principle - only handles Course business logic
 */
public class CourseServiceImpl implements CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public CourseServiceImpl(CourseRepository courseRepository, 
                           StudentRepository studentRepository,
                           TrainerRepository trainerRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public void createCourse(String name, String startDate, String finishDate, int countPlaces) {
        if (name == null || name.trim().isEmpty()) {
            logger.warn("Cannot create course with empty name");
            return;
        }
        
        try {
            Date beginDate = dateFormat.parse(startDate);
            Date endDate = dateFormat.parse(finishDate);
            
            Course course = new Course(name, "", beginDate, endDate, "");
            course.setCountPlaces(countPlaces);
            
            courseRepository.save(course);
            logger.info("Course created: {}", name);
            
        } catch (ParseException e) {
            logger.error("Invalid date format for course {}: {}", name, e.getMessage());
        }
    }

    @Override
    public Optional<Course> getCourse(int id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getFreeCourses() {
        return courseRepository.findFreeCourses();
    }

    @Override
    public void updateCourse(Course course) {
        if (course != null) {
            courseRepository.update(course);
        }
    }

    @Override
    public void deleteCourse(int id) {
        Optional<Course> course = courseRepository.findById(id);
        if (course.isPresent()) {
            Course courseToDelete = course.get();
            
            // Remove course from all enrolled students
            List<Student> enrolledStudents = studentRepository.findByCourseId(id);
            for (Student student : enrolledStudents) {
                student.deleteCourse(courseToDelete);
                studentRepository.update(student);
            }
            
            // Remove trainer assignment
            if (courseToDelete.getTrainer() != null) {
                Trainer trainer = courseToDelete.getTrainer();
                trainer.deleteCourse(courseToDelete);
                trainerRepository.update(trainer);
            }
            
            courseRepository.deleteById(id);
            logger.info("Course deleted: {}", courseToDelete.getName());
        }
    }

    @Override
    public boolean enrollStudent(int courseId, int studentId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        
        if (!courseOpt.isPresent() || !studentOpt.isPresent()) {
            logger.warn("Course or Student not found for enrollment");
            return false;
        }
        
        Course course = courseOpt.get();
        Student student = studentOpt.get();
        
        if (course.getCountPlaces() <= 0) {
            logger.warn("No available places in course: {}", course.getName());
            return false;
        }
        
        if (course.addStudent(student)) {
            student.addCourse(course);
            courseRepository.update(course);
            studentRepository.update(student);
            logger.info("Student {} enrolled in course {}", student.getName(), course.getName());
            return true;
        }
        
        return false;
    }

    @Override
    public boolean removeStudent(int courseId, int studentId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        
        if (!courseOpt.isPresent() || !studentOpt.isPresent()) {
            logger.warn("Course or Student not found for removal");
            return false;
        }
        
        Course course = courseOpt.get();
        Student student = studentOpt.get();
        
        course.deleteStudent(student);
        student.deleteCourse(course);
        
        courseRepository.update(course);
        studentRepository.update(student);
        
        logger.info("Student {} removed from course {}", student.getName(), course.getName());
        return true;
    }

    @Override
    public List<Student> getStudentsInCourse(int courseId) {
        return studentRepository.findByCourseId(courseId);
    }

    @Override
    public boolean assignTrainer(int courseId, int trainerId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        Optional<Trainer> trainerOpt = trainerRepository.findById(trainerId);
        
        if (!courseOpt.isPresent() || !trainerOpt.isPresent()) {
            logger.warn("Course or Trainer not found for assignment");
            return false;
        }
        
        Course course = courseOpt.get();
        Trainer trainer = trainerOpt.get();
        
        course.setTrainer(trainer);
        trainer.addCourse(course);
        
        courseRepository.update(course);
        trainerRepository.update(trainer);
        
        logger.info("Trainer {} assigned to course {}", trainer.getName(), course.getName());
        return true;
    }

    @Override
    public boolean removeTrainer(int courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        
        if (!courseOpt.isPresent()) {
            logger.warn("Course not found for trainer removal");
            return false;
        }
        
        Course course = courseOpt.get();
        Trainer trainer = course.getTrainer();
        
        if (trainer != null) {
            course.setTrainer(null);
            trainer.deleteCourse(course);
            
            courseRepository.update(course);
            trainerRepository.update(trainer);
            
            logger.info("Trainer removed from course {}", course.getName());
            return true;
        }
        
        return false;
    }

    @Override
    public Optional<Trainer> getCourseTrainer(int courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        return courseOpt.map(Course::getTrainer);
    }

    @Override
    public int getAvailablePlaces(int courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        return courseOpt.map(Course::getCountPlaces).orElse(0);
    }

    @Override
    public int getEnrolledStudentsCount(int courseId) {
        return studentRepository.findByCourseId(courseId).size();
    }
}
