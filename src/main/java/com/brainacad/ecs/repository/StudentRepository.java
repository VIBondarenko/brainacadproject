package com.brainacad.ecs.repository;

import com.brainacad.ecs.Student;
import java.util.List;
import java.util.Optional;

/**
 * Student repository interface extending generic repository
 */
public interface StudentRepository extends Repository<Student, Integer> {
    List<Student> findByCourseId(int courseId);
    Optional<Student> findByName(String name);
    List<Student> findByAge(int age);
}
