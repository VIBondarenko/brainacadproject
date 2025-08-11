package com.brainacad.ecs.repository;

import com.brainacad.ecs.Task;
import java.util.List;
import java.util.Optional;

/**
 * Task repository interface extending generic repository
 */
public interface TaskRepository extends Repository<Task, Integer> {
    List<Task> findByCourseId(int courseId);
    List<Task> findByStudentId(int studentId);
    Optional<Task> findByName(String name);
}
