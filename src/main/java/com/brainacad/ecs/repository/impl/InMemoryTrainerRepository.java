package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.Trainer;
import com.brainacad.ecs.repository.TrainerRepository;
import com.brainacad.ecs.Utilities;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * In-memory implementation of TrainerRepository
 * Follows Single Responsibility Principle - only handles Trainer data operations
 */
public class InMemoryTrainerRepository implements TrainerRepository {
    private static final Logger logger = Logger.getLogger(InMemoryTrainerRepository.class.getName());
    private final List<Trainer> trainers = new ArrayList<>();

    @Override
    public void save(Trainer trainer) {
        if (trainer == null) {
            logger.log(Level.WARNING, "Attempted to save null trainer");
            return;
        }
        if (!exists(trainer.getId())) {
            trainers.add(trainer);
            logger.log(Level.INFO, "Trainer saved: {0} {1}", new Object[]{trainer.getName(), trainer.getLastName()});
        } else {
            update(trainer);
        }
    }

    @Override
    public Optional<Trainer> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return trainers.stream()
                .filter(trainer -> trainer.getId() == id)
                .findFirst();
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(trainers);
    }

    @Override
    public void update(Trainer trainer) {
        if (trainer == null) {
            logger.log(Level.WARNING, "Attempted to update null trainer");
            return;
        }
        
        Optional<Trainer> existingTrainer = findById(trainer.getId());
        if (existingTrainer.isPresent()) {
            int index = trainers.indexOf(existingTrainer.get());
            trainers.set(index, trainer);
            logger.log(Level.INFO, "Trainer updated: {0} {1}", new Object[]{trainer.getName(), trainer.getLastName()});
        } else {
            logger.log(Level.WARNING, "Trainer with ID {0} not found for update", trainer.getId());
        }
    }

    @Override
    public void delete(Trainer trainer) {
        if (trainer != null) {
            deleteById(trainer.getId());
        }
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            logger.log(Level.WARNING, "Attempted to delete trainer with null ID");
            return;
        }
        
        boolean removed = trainers.removeIf(trainer -> trainer.getId() == id);
        if (removed) {
            logger.log(Level.INFO, "Trainer deleted with ID: {0}", id);
        } else {
            logger.log(Level.WARNING, "Trainer with ID {0} not found for deletion", id);
        }
    }

    @Override
    public boolean exists(Integer id) {
        return id != null && trainers.stream()
                .anyMatch(trainer -> trainer.getId() == id);
    }

    @Override
    public long count() {
        return trainers.size();
    }

    @Override
    public List<Trainer> findByCourseId(int courseId) {
        return trainers.stream()
                .filter(trainer -> trainer.getCourses().stream()
                        .anyMatch(course -> course.getId() == courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Trainer> findByName(String name) {
        return Utilities.findByName(trainers, name);
    }

    @Override
    public List<Trainer> findFreeByCourseId(int courseId) {
        // Return trainers who are not assigned to the specified course
        return trainers.stream()
                .filter(trainer -> trainer.getCourses().stream()
                        .noneMatch(course -> course.getId() == courseId))
                .collect(Collectors.toList());
    }
}
