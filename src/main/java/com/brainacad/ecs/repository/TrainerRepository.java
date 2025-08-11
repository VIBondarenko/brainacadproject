package com.brainacad.ecs.repository;

import com.brainacad.ecs.Trainer;
import java.util.List;
import java.util.Optional;

/**
 * Trainer repository interface extending generic repository
 */
public interface TrainerRepository extends Repository<Trainer, Integer> {
    List<Trainer> findByCourseId(int courseId);
    Optional<Trainer> findByName(String name);
    List<Trainer> findFreeByCourseId(int courseId);
}
