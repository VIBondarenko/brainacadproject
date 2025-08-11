package com.brainacad.ecs.service;

import java.io.IOException;

/**
 * Data persistence service interface
 * Follows Single Responsibility Principle - only handles data persistence
 */
public interface PersistenceService {
    void saveData() throws IOException;
    void loadData() throws IOException, ClassNotFoundException;
    void saveData(String fileName) throws IOException;
    void loadData(String fileName) throws IOException, ClassNotFoundException;
    boolean dataExists();
    boolean dataExists(String fileName);
}
