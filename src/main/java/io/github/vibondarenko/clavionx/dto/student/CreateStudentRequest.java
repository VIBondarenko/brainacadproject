package io.github.vibondarenko.clavionx.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new student
 */
public record CreateStudentRequest(
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot exceed 50 characters")
    String name,
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    String lastName,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    String email,
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    String phoneNumber,
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username, // Optional - will be generated if not provided
    
    @NotNull(message = "Graduation year is required")
    @Min(value = 2024, message = "Graduation year cannot be in the past")
    @Max(value = 2035, message = "Graduation year cannot be too far in the future")
    Integer graduationYear
) {
}