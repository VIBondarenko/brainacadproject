package io.github.vibondarenko.clavionx.dto.student;

import java.time.LocalDate;

/**
 * DTO for student list view - minimal information for table display
 */
public record StudentListDto(
    Long id,
    String fullName,
    String studentNumber,
    String email,
    LocalDate enrollmentDate,
    Integer graduationYear,
    int coursesCount,
    boolean enabled
) {
    
    public static StudentListDto from(io.github.vibondarenko.clavionx.entity.Student student) {
        return new StudentListDto(
            student.getId(),
            student.getName() + " " + student.getLastName(),
            student.getStudentNumber(),
            student.getEmail(),
            student.getEnrollmentDate(),
            student.getGraduationYear(),
            student.getCourses().size(),
            student.isEnabled()
        );
    }
}