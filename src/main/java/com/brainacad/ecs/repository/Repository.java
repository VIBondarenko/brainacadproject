package com.brainacad.ecs.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface following SOLID principles
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface Repository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
    void deleteById(ID id);
    boolean exists(ID id);
    long count();
}
