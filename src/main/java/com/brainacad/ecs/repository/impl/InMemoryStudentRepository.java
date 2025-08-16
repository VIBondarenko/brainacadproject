package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.Utilities;
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * In-memory implementation of StudentRepository
 * Follows Single Responsibility Principle - only handles Student data operations
 */
public class InMemoryStudentRepository implements StudentRepository {
    private static final Logger logger = Logger.getLogger(InMemoryStudentRepository.class.getName());
    private final List<Student> students = new ArrayList<>();

    @Override
    public void save(Student student) {
        if (student == null) {
            logger.log(Level.WARNING, "Attempted to save null student");
            return;
        }
        if (!exists(student.getId())) {
            students.add(student);
            logger.log(Level.INFO, "Student saved: {0} {1}", new Object[]{student.getName(), student.getLastName()});
        } else {
            update(student);
        }
    }

    @Override
    public Optional<Student> findById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return students.stream()
                .filter(student -> student.getId() == id)
                .findFirst();
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    @Override
    public void update(Student student) {
        if (student == null) {
            logger.log(Level.WARNING, "Attempted to update null student");
            return;
        }
        
        Optional<Student> existingStudent = findById(student.getId());
        if (existingStudent.isPresent()) {
            int index = students.indexOf(existingStudent.get());
            students.set(index, student);
            logger.log(Level.INFO, "Student updated: {0} {1}", new Object[]{student.getName(), student.getLastName()});
        } else {
            logger.log(Level.WARNING, "Student with ID {0} not found for update", student.getId());
        }
    }

    @Override
    public void delete(Student student) {
        if (student != null) {
            deleteById(student.getId());
        }
    }

    @Override
    public void deleteById(Integer id) {
        if (id == null) {
            logger.log(Level.WARNING, "Attempted to delete student with null ID");
            return;
        }
        
        boolean removed = students.removeIf(student -> student.getId() == id);
        if (removed) {
            logger.log(Level.INFO, "Student deleted with ID: {0}", id);
        } else {
            logger.log(Level.WARNING, "Student with ID {0} not found for deletion", id);
        }
    }

    @Override
    public boolean exists(Integer id) {
        return id != null && students.stream()
                .anyMatch(student -> student.getId() == id);
    }

    @Override
    public long count() {
        return students.size();
    }

    @Override
    public List<Student> findByCourseId(int courseId) {
        return students.stream()
                .filter(student -> student.getCourses().stream()
                        .anyMatch(course -> course.getId() == courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Student> findByName(String name) {
        return Utilities.findByName(students, name);
    }

    @Override
    public List<Student> findByAge(int age) {
        return students.stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }
}
