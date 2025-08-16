package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.entity.Task;
import com.brainacad.ecs.repository.TaskRepository;
import com.brainacad.ecs.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TaskRepository
 * Follows Single Responsibility Principle - only handles Task data operations
 */
public class InMemoryTaskRepository implements TaskRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryTaskRepository.class);
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public void save(Task task) {
        if (task == null) {
            logger.warn("Attempted to save null task");
            return;
        }
        if (!exists(task.getId())) {
            tasks.add(task);
            logger.info("Task saved: {}", task.getName());
        } else {
            update(task);
        }
    }

    @Override
    public Optional<Task> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public void update(Task task) {
        if (task == null) {
            logger.warn("Attempted to update null task");
            return;
        }
        
        Optional<Task> existingTask = findById(task.getId());
        if (existingTask.isPresent()) {
            int index = tasks.indexOf(existingTask.get());
            tasks.set(index, task);
            logger.info("Task updated: {}", task.getName());
        } else {
            logger.warn("Task with ID {} not found for update", task.getId());
        }
    }

    @Override
    public void delete(Task task) {
        if (task != null) {
            deleteById(task.getId());
        }
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            logger.warn("Attempted to delete task with null ID");
            return;
        }
        
        boolean removed = tasks.removeIf(task -> task.getId() == id);
        if (removed) {
            logger.info("Task deleted with ID: {}", id);
        } else {
            logger.warn("Task with ID {} not found for deletion", id);
        }
    }

    @Override
    public boolean exists(Integer id) {
        return id != null && tasks.stream()
                .anyMatch(task -> task.getId() == id);
    }

    @Override
    public long count() {
        return tasks.size();
    }

    @Override
    public List<Task> findByCourseId(int courseId) {
        return tasks.stream()
                .filter(task -> task.getCourse() != null && task.getCourse().getId() == courseId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByStudentId(int studentId) {
        // This would require checking which tasks are assigned to specific students
        // Implementation depends on how task-student relationships are managed
        // For now, return empty list
        logger.info("findByStudentId not fully implemented - requires task-student relationship data");
        return new ArrayList<>();
    }

    @Override
    public Optional<Task> findByName(String name) {
        return Utilities.findByName(tasks, name);
    }
}
