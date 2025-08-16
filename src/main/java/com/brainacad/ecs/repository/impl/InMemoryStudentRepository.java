package com.brainacad.ecs.repository.impl;

import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.repository.StudentRepository;
import com.brainacad.ecs.Utilities;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of StudentRepository
 * Extends AbstractInMemoryRepository to eliminate code duplication
 * Follows Single Responsibility Principle - only handles Student data operations
 */
public class InMemoryStudentRepository extends AbstractInMemoryRepository<Student, Integer> 
        implements StudentRepository {

    // Only Student-specific methods from StudentRepository interface

    @Override
    public List<Student> findByCourseId(int courseId) {
        return items.stream()
                .filter(student -> student.getCourses().stream()
                        .anyMatch(course -> course.getId() == courseId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Student> findByName(String name) {
        return Utilities.findByName(items, name);
    }

    @Override
    public List<Student> findByAge(int age) {
        return items.stream()
                .filter(student -> student.getAge() == age)
                .collect(Collectors.toList());
    }

    // Implementation of abstract methods from AbstractInMemoryRepository
    
    @Override
    protected String getEntityName() {
        return "Student";
    }
    
    @Override
    protected Integer getId(Student entity) {
        return entity.getId();
    }
    
    @Override
    protected String getLogMessage(Student entity) {
        return entity.getName() + " " + entity.getLastName();
    }
}
