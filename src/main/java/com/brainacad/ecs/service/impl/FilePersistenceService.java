package com.brainacad.ecs.service.impl;

import com.brainacad.ecs.service.PersistenceService;
import com.brainacad.ecs.repository.CourseRepository;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.repository.TrainerRepository;
import com.brainacad.ecs.repository.TaskRepository;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * File-based persistence service implementation
 * Follows Single Responsibility Principle - only handles data persistence
 */
public class FilePersistenceService implements PersistenceService {
    private static final Logger logger = Logger.getLogger(FilePersistenceService.class.getName());
    private static final String DEFAULT_FILENAME = "Storage";
    private static final String FILE_EXTENSION = ".ser";
    
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TrainerRepository trainerRepository;
    private final TaskRepository taskRepository;

    public FilePersistenceService(CourseRepository courseRepository, 
                                StudentRepository studentRepository,
                                TrainerRepository trainerRepository,
                                TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.trainerRepository = trainerRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void saveData() throws IOException {
        saveData(DEFAULT_FILENAME);
    }

    @Override
    public void saveData(String fileName) throws IOException {
        String fullFileName = fileName + FILE_EXTENSION;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(fullFileName))) {
            
            // Save all data in a structured format
            PersistenceData data = new PersistenceData();
            data.courses = courseRepository.findAll();
            data.students = studentRepository.findAll();
            data.trainers = trainerRepository.findAll();
            data.tasks = taskRepository.findAll();
            
            oos.writeObject(data);
            logger.log(Level.INFO, "Data saved successfully to {0}", fullFileName);
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save data to {0}: {1}", 
                      new Object[]{fullFileName, e.getMessage()});
            throw e;
        }
    }

    @Override
    public void loadData() throws IOException, ClassNotFoundException {
        loadData(DEFAULT_FILENAME);
    }

    @Override
    public void loadData(String fileName) throws IOException, ClassNotFoundException {
        String fullFileName = fileName + FILE_EXTENSION;
        
        if (!dataExists(fileName)) {
            logger.log(Level.WARNING, "Data file {0} does not exist", fullFileName);
            throw new IOException("File not found: " + fullFileName);
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(fullFileName))) {
            
            PersistenceData data = (PersistenceData) ois.readObject();
            
            // Load data into repositories
            if (data.courses != null) {
                data.courses.forEach(courseRepository::save);
            }
            if (data.students != null) {
                data.students.forEach(studentRepository::save);
            }
            if (data.trainers != null) {
                data.trainers.forEach(trainerRepository::save);
            }
            if (data.tasks != null) {
                data.tasks.forEach(taskRepository::save);
            }
            
            logger.log(Level.INFO, "Data loaded successfully from {0}", fullFileName);
            
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to load data from {0}: {1}", 
                      new Object[]{fullFileName, e.getMessage()});
            throw e;
        }
    }

    @Override
    public boolean dataExists() {
        return dataExists(DEFAULT_FILENAME);
    }

    @Override
    public boolean dataExists(String fileName) {
        String fullFileName = fileName + FILE_EXTENSION;
        File file = new File(fullFileName);
        return file.exists() && file.isFile();
    }

    /**
     * Internal class to hold all persistence data
     */
    private static class PersistenceData implements Serializable {
        private static final long serialVersionUID = 100L;
        java.util.List<com.brainacad.ecs.entity.Course> courses;
        java.util.List<com.brainacad.ecs.entity.Student> students;
        java.util.List<com.brainacad.ecs.entity.Trainer> trainers;
        java.util.List<com.brainacad.ecs.entity.Task> tasks;
    }
}
