package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.Utilities;
import com.brainacad.ecs.entity.Course;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * In-memory implementation of CourseRepository
 * Follows Single Responsibility Principle - only handles Course data operations
 */
public class InMemoryCourseRepository implements CourseRepository {
    private static final Logger logger = Logger.getLogger(InMemoryCourseRepository.class.getName());
    private final List<Course> courses = new ArrayList<>();
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public void save(Course course) {
        if (course == null) {
            logger.log(Level.WARNING, "Attempted to save null course");
            return;
        }
        if (!exists(course.getId())) {
            courses.add(course);
            logger.log(Level.INFO, "Course saved: {0}", course.getName());
        } else {
            update(course);
        }
    }

    @Override
    public Optional<Course> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return courses.stream()
                .filter(course -> course.getId() == id)
                .findFirst();
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(courses);
    }

    @Override
    public void update(Course course) {
        if (course == null) {
            logger.log(Level.WARNING, "Attempted to update null course");
            return;
        }
        
        Optional<Course> existingCourse = findById(course.getId());
        if (existingCourse.isPresent()) {
            int index = courses.indexOf(existingCourse.get());
            courses.set(index, course);
            logger.log(Level.INFO, "Course updated: {0}", course.getName());
        } else {
            logger.log(Level.WARNING, "Course with ID {0} not found for update", course.getId());
        }
    }

    @Override
    public void delete(Course course) {
        if (course != null) {
            deleteById(course.getId());
        }
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            logger.log(Level.WARNING, "Attempted to delete course with null ID");
            return;
        }
        
        boolean removed = courses.removeIf(course -> course.getId() == id);
        if (removed) {
            logger.log(Level.INFO, "Course deleted with ID: {0}", id);
        } else {
            logger.log(Level.WARNING, "Course with ID {0} not found for deletion", id);
        }
    }

    @Override
    public boolean exists(Integer id) {
        return id != null && courses.stream()
                .anyMatch(course -> course.getId() == id);
    }

    @Override
    public long count() {
        return courses.size();
    }

    @Override
    public List<Course> findByTrainerId(int trainerId) {
        return courses.stream()
                .filter(course -> course.getTrainer() != null && course.getTrainer().getId() == trainerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findByStudentId(int studentId) {
        return courses.stream()
                .filter(course -> course.getStudents().stream()
                        .anyMatch(student -> student.getId() == studentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findFreeCourses() {
        return courses.stream()
                .filter(course -> course.getCountPlaces() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Course> findByName(String name) {
        return Utilities.findByName(courses, name);
    }

    @Override
    public List<Course> findByDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            
            return courses.stream()
                    .filter(course -> {
                        try {
                            Date courseStart = course.getBeginDate();
                            Date courseFinish = course.getEndDate();
                            return (courseStart != null && courseFinish != null) &&
                                   ((courseStart.compareTo(start) >= 0 && courseStart.compareTo(end) <= 0) ||
                                   (courseFinish.compareTo(start) >= 0 && courseFinish.compareTo(end) <= 0));
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Error processing course dates for course: {0}", course.getName());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            logger.log(Level.WARNING, "Error parsing date range: {0} - {1}", new Object[]{startDate, endDate});
            return new ArrayList<>();
        }
    }
}
