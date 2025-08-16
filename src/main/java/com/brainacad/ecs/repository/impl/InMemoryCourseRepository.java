package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.Utilities;
import com.brainacad.ecs.entity.Course;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of CourseRepository
 * Extends AbstractInMemoryRepository to eliminate code duplication
 * Follows Single Responsibility Principle - only handles Course data operations
 */
public class InMemoryCourseRepository extends AbstractInMemoryRepository<Course, Integer> 
        implements CourseRepository {
    
    private final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    // Only Course-specific methods from CourseRepository interface

    @Override
    public List<Course> findByTrainerId(int trainerId) {
        return items.stream()
                .filter(course -> course.getTrainer() != null && course.getTrainer().getId() == trainerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findByStudentId(int studentId) {
        return items.stream()
                .filter(course -> course.getStudents().stream()
                        .anyMatch(student -> student.getId() == studentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Course> findFreeCourses() {
        return items.stream()
                .filter(course -> course.getCountPlaces() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Course> findByName(String name) {
        return Utilities.findByName(items, name);
    }

    @Override
    public List<Course> findByDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return new ArrayList<>();
        }
        
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            
            return items.stream()
                    .filter(course -> {
                        try {
                            Date courseStart = course.getBeginDate();
                            Date courseFinish = course.getEndDate();
                            return (courseStart != null && courseFinish != null) &&
                                   ((courseStart.compareTo(start) >= 0 && courseStart.compareTo(end) <= 0) ||
                                   (courseFinish.compareTo(start) >= 0 && courseFinish.compareTo(end) <= 0));
                        } catch (Exception e) {
                            logger.warn("Error processing course dates for course: {}", course.getName());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (ParseException e) {
            logger.warn("Error parsing date range: {} - {}", startDate, endDate);
            return new ArrayList<>();
        }
    }

    // Implementation of abstract methods from AbstractInMemoryRepository
    
    @Override
    protected String getEntityName() {
        return "Course";
    }
    
    @Override
    protected Integer getId(Course entity) {
        return entity.getId();
    }
    
    @Override
    protected String getLogMessage(Course entity) {
        return entity.getName();
    }
}
