package com.brainacad.ecs.repository;

import com.brainacad.ecs.Course;
import java.util.List;
import java.util.Optional;

/**
 * Course repository interface extending generic repository
 */
public interface CourseRepository extends Repository<Course, Integer> {
    List<Course> findByTrainerId(int trainerId);
    List<Course> findByStudentId(int studentId);
    List<Course> findFreeCourses();
    Optional<Course> findByName(String name);
    List<Course> findByDateRange(String startDate, String endDate);
}
