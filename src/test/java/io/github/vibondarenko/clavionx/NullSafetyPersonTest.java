package io.github.vibondarenko.clavionx;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.vibondarenko.clavionx.entity.Student;

/**
 * Test class to verify null safety in Person equals() and hashCode() methods
 */
public class NullSafetyPersonTest {

    @Test
    @DisplayName("Test equals() method with null name and lastName - should not throw NullPointerException")
    void testEqualsWithNullFields() {
        // Create students with same name/lastName (null) - they should be equal according to Person.equals()
        Student student1 = new Student(null, null, "user1", "pass1", "test1@email.com");
        Student student2 = new Student(null, null, "user2", "pass2", "test2@email.com");
        Student student3 = new Student("John", "Doe", "user3", "pass3", "test3@email.com");
        
        // These should not throw NullPointerException
        assertDoesNotThrow(() -> student1.equals(student2), 
            "equals() should handle null fields without throwing NPE");
        assertDoesNotThrow(() -> student1.equals(student3), 
            "equals() should handle null vs non-null fields without throwing NPE");
        assertDoesNotThrow(() -> student3.equals(student1), 
            "equals() should handle non-null vs null fields without throwing NPE");
        
        // Test actual equality (Person.equals() compares id, name, lastName, age - username is not considered)
        assertTrue(student1.equals(student2), "Students with same null name/lastName should be equal");
        assertFalse(student1.equals(student3), "Students with different name/lastName should not be equal");
    }

    @Test
    @DisplayName("Test hashCode() method with null fields - should not throw NullPointerException")
    void testHashCodeWithNullFields() {
        Student studentWithNulls = new Student(null, null, "user1", "pass1", "test@email.com");
        
        // This should not throw NullPointerException
        assertDoesNotThrow(() -> studentWithNulls.hashCode(), 
            "hashCode() should handle null fields without throwing NPE");
        
        // Should return a valid hash code
        int hashCode = studentWithNulls.hashCode();
        assertNotEquals(0, hashCode, "hashCode should return non-zero value even with null fields");
    }

    @Test
    @DisplayName("Test mixed null and non-null scenarios")
    void testMixedNullScenarios() {
        Student student1 = new Student("John", null, "user1", "pass1", "test1@email.com");  // null lastName
        Student student2 = new Student(null, "Doe", "user2", "pass2", "test2@email.com");   // null name
        Student student3 = new Student("John", null, "user3", "pass3", "test3@email.com");  // same name/lastName as student1
        
        // Should not throw exceptions
        assertDoesNotThrow(() -> {
            student1.equals(student2);
            student1.hashCode();
            student2.hashCode();
        });
        
        // Test equality (Person.equals() only compares id, name, lastName, age)
        assertTrue(student1.equals(student3), "Students with same name/lastName should be equal");
        assertFalse(student1.equals(student2), "Students with different name/lastName should not be equal");
    }
}



