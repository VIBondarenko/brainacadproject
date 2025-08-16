package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Abstract base class for InMemory repository implementations
 * Eliminates code duplication between concrete repository implementations
 * Follows Template Method Pattern
 * 
 * @param <T> Entity type
 * @param <ID> ID type
 */
public abstract class AbstractInMemoryRepository<T, ID> implements Repository<T, ID> {
    protected final List<T> items = new ArrayList<>();
    protected final Logger logger;
    
    public AbstractInMemoryRepository() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void save(T entity) {
        if (entity == null) {
            logger.warn("Attempted to save null {}", getEntityName().toLowerCase());
            return;
        }
        if (!exists(getId(entity))) {
            items.add(entity);
            logger.info("{} saved: {}", getEntityName(), getLogMessage(entity));
        } else {
            update(entity);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return items.stream()
                .filter(item -> getId(item).equals(id))
                .findFirst();
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(items);
    }

    @Override
    public void update(T entity) {
        if (entity == null) {
            logger.warn("Attempted to update null {}", getEntityName().toLowerCase());
            return;
        }
        
        Optional<T> existingEntity = findById(getId(entity));
        if (existingEntity.isPresent()) {
            int index = items.indexOf(existingEntity.get());
            items.set(index, entity);
            logger.info("{} updated: {}", getEntityName(), getLogMessage(entity));
        } else {
            logger.warn("{} with ID {} not found for update", getEntityName(), getId(entity));
        }
    }

    @Override
    public void delete(T entity) {
        if (entity != null) {
            deleteById(getId(entity));
        }
    }

    @Override
    public void deleteById(ID id) {
        if (id == null) {
            logger.warn("Attempted to delete {} with null ID", getEntityName().toLowerCase());
            return;
        }
        
        boolean removed = items.removeIf(item -> getId(item).equals(id));
        if (removed) {
            logger.info("{} deleted with ID: {}", getEntityName(), id);
        } else {
            logger.warn("{} with ID {} not found for deletion", getEntityName(), id);
        }
    }

    @Override
    public boolean exists(ID id) {
        return id != null && items.stream()
                .anyMatch(item -> getId(item).equals(id));
    }

    @Override
    public long count() {
        return items.size();
    }

    // Abstract methods to be implemented by concrete classes
    
    /**
     * Get the name of the entity for logging purposes
     * @return Entity name (e.g., "Student", "Course", "Trainer")
     */
    protected abstract String getEntityName();
    
    /**
     * Extract ID from entity
     * @param entity The entity
     * @return Entity ID
     */
    protected abstract ID getId(T entity);
    
    /**
     * Get log message for entity (used in save/update logging)
     * @param entity The entity
     * @return Log message string
     */
    protected abstract String getLogMessage(T entity);
}
