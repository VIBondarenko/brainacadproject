package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.entity.Trainer;
import com.brainacad.ecs.repository.TrainerRepository;
import com.brainacad.ecs.Utilities;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of TrainerRepository
 * Extends AbstractInMemoryRepository to eliminate code duplication
 * Follows Single Responsibility Principle - only handles Trainer data operations
 */
public class InMemoryTrainerRepository extends AbstractInMemoryRepository<Trainer, Integer> 
        implements TrainerRepository {

    // Only Trainer-specific methods from TrainerRepository interface

    @Override
    public List<Trainer> findByCourseId(int courseId) {
        return items.stream()
                .filter(trainer -> trainer.getCourses().stream()
                        .anyMatch(course -> course.getId() == courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Trainer> findByName(String name) {
        return Utilities.findByName(items, name);
    }

    @Override
    public List<Trainer> findFreeByCourseId(int courseId) {
        // Return trainers who are not assigned to the specified course
        return items.stream()
                .filter(trainer -> trainer.getCourses().stream()
                        .noneMatch(course -> course.getId() == courseId))
                .collect(Collectors.toList());
    }

    // Implementation of abstract methods from AbstractInMemoryRepository
    
    @Override
    protected String getEntityName() {
        return "Trainer";
    }
    
    @Override
    protected Integer getId(Trainer entity) {
        return entity.getId();
    }
    
    @Override
    protected String getLogMessage(Trainer entity) {
        return entity.getName() + " " + entity.getLastName();
    }
}
