package com.brainacad.ecs;

import com.brainacad.ecs.entity.Student;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify null safety in Person equals() and hashCode() methods
 */
public class NullSafetyPersonTest {

    @Test
    @DisplayName("Test equals() method with null name and lastName - should not throw NullPointerException")
    public void testEqualsWithNullFields() {
        // Create a student with null name and lastName
        Student student1 = new Student(1, null, null, 20);
        Student student2 = new Student(1, null, null, 20);
        Student student3 = new Student(2, "John", "Doe", 20);
        
        // These should not throw NullPointerException
        assertDoesNotThrow(() -> student1.equals(student2), 
            "equals() should handle null fields without throwing NPE");
        assertDoesNotThrow(() -> student1.equals(student3), 
            "equals() should handle null vs non-null fields without throwing NPE");
        assertDoesNotThrow(() -> student3.equals(student1), 
            "equals() should handle non-null vs null fields without throwing NPE");
        
        // Test actual equality
        assertTrue(student1.equals(student2), "Students with same null fields should be equal");
        assertFalse(student1.equals(student3), "Students with different fields should not be equal");
    }

    @Test
    @DisplayName("Test hashCode() method with null fields - should not throw NullPointerException")
    public void testHashCodeWithNullFields() {
        Student studentWithNulls = new Student(1, null, null, 25);
        
        // This should not throw NullPointerException
        assertDoesNotThrow(() -> studentWithNulls.hashCode(), 
            "hashCode() should handle null fields without throwing NPE");
        
        // Should return a valid hash code
        int hashCode = studentWithNulls.hashCode();
        assertNotEquals(0, hashCode, "hashCode should return non-zero value even with null fields");
    }

    @Test
    @DisplayName("Test mixed null and non-null scenarios")
    public void testMixedNullScenarios() {
        Student student1 = new Student(1, "John", null, 20);  // null lastName
        Student student2 = new Student(1, null, "Doe", 20);   // null name
        Student student3 = new Student(1, "John", null, 20);  // same as student1
        
        // Should not throw exceptions
        assertDoesNotThrow(() -> {
            student1.equals(student2);
            student1.hashCode();
            student2.hashCode();
        });
        
        // Test equality
        assertTrue(student1.equals(student3), "Students with same fields (including nulls) should be equal");
        assertFalse(student1.equals(student2), "Students with different null fields should not be equal");
    }
}
