package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.repository.Repository;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

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
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    @Override
    public void save(T entity) {
        if (entity == null) {
            logger.log(Level.WARNING, "Attempted to save null {0}", getEntityName().toLowerCase());
            return;
        }
        if (!exists(getId(entity))) {
            items.add(entity);
            logger.log(Level.INFO, "{0} saved: {1}", new Object[]{getEntityName(), getLogMessage(entity)});
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
            logger.log(Level.WARNING, "Attempted to update null {0}", getEntityName().toLowerCase());
            return;
        }
        
        Optional<T> existingEntity = findById(getId(entity));
        if (existingEntity.isPresent()) {
            int index = items.indexOf(existingEntity.get());
            items.set(index, entity);
            logger.log(Level.INFO, "{0} updated: {1}", new Object[]{getEntityName(), getLogMessage(entity)});
        } else {
            logger.log(Level.WARNING, "{0} with ID {1} not found for update", 
                    new Object[]{getEntityName(), getId(entity)});
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
            logger.log(Level.WARNING, "Attempted to delete {0} with null ID", getEntityName().toLowerCase());
            return;
        }
        
        boolean removed = items.removeIf(item -> getId(item).equals(id));
        if (removed) {
            logger.log(Level.INFO, "{0} deleted with ID: {1}", new Object[]{getEntityName(), id});
        } else {
            logger.log(Level.WARNING, "{0} with ID {1} not found for deletion", 
                    new Object[]{getEntityName(), id});
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
