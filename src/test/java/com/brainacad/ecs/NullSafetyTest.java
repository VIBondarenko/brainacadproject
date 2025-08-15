package com.brainacad.ecs;

import org.junit.Test;

import com.brainacad.ecs.entity.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.Assert.*;

/**
 * Tests for null safety improvements
 * Author: Vitaliy Bondarenko (via Code Review)
 */
public class NullSafetyTest {

    @Test
    public void testSearchByNameWithNull() {
        List<Student> students = new ArrayList<>();
        
        // Test with null list
        Student result = Utilities.searchByName(null, "Test");
        assertNull("Should return null for null list", result);
        
        // Test with null search string
        result = Utilities.searchByName(students, null);
        assertNull("Should return null for null search string", result);
    }

    @Test
    public void testSearchByIdWithNull() {
        // Test with null list
        Student result = Utilities.searchById(null, 1);
        assertNull("Should return null for null list", result);
    }

    @Test
    public void testOptionalBasedSearch() {
        List<Student> students = new ArrayList<>();
        students.add(new Student("John", "Doe"));
        students.add(new Student("Jane", "Smith"));
        
        // Test successful find
        Optional<Student> found = Utilities.findByName(students, "John");
        assertTrue("Should find student John", found.isPresent());
        assertEquals("John", found.get().getName());
        
        // Test not found
        Optional<Student> notFound = Utilities.findByName(students, "NonExistent");
        assertFalse("Should not find non-existent student", notFound.isPresent());
        
        // Test with null inputs
        Optional<Student> nullResult = Utilities.findByName(null, "Test");
        assertFalse("Should return empty Optional for null list", nullResult.isPresent());
    }

    @Test
    public void testCourseAddStudentWithNull() {
        Course course = new Course("Test Course", "Description", 
                                  new java.util.Date(), new java.util.Date(), "MON TUE");
        
        // Test adding null student
        Boolean result = course.addStudent(null);
        assertFalse("Should not add null student", result);
    }

    @Test
    public void testStudentAddCourseWithNull() {
        Student student = new Student("Test", "Student");
        
        // This should not crash and should handle null gracefully
        student.addCourse(null);
        // If we reach here, null was handled properly
        assertTrue("Should handle null course gracefully", true);
    }

    @Test
    public void testStudentAddTaskWithNull() {
        Student student = new Student("Test", "Student");
        
        // Test adding null task
        boolean result = student.addTask(null);
        assertFalse("Should not add null task", result);
    }

    @Test
    public void testStudentAddTasksWithNull() {
        Student student = new Student("Test", "Student");
        
        // This should not crash and should handle null gracefully
        student.addTasks(null, 1);
        // If we reach here, null was handled properly
        assertTrue("Should handle null tasks list gracefully", true);
    }

    @Test
    public void testListToStringWithNull() {
        // Test with null list
        String result = Utilities.listToString(null);
        assertEquals("Should return empty string for null list", "", result);
        
        // Test with list containing null elements
        List<Student> students = new ArrayList<>();
        students.add(new Student("John", "Doe"));
        students.add(null);  // Add null element
        students.add(new Student("Jane", "Smith"));
        
        String listString = Utilities.listToString(students);
        // Should not crash and should skip null elements
        assertTrue("Should handle null elements gracefully", listString.contains("John"));
        assertTrue("Should handle null elements gracefully", listString.contains("Jane"));
    }
}
