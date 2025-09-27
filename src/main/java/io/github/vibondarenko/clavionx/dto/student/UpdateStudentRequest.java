package io.github.vibondarenko.clavionx.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing student
 */
public record UpdateStudentRequest(
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    String name,
    
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    String lastName,
    
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phoneNumber,
    
    @Min(value = 2024, message = "Graduation year cannot be in the past")
    @Max(value = 2035, message = "Graduation year cannot be too far in the future")
    Integer graduationYear
) {
}