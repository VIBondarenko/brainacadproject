package io.github.vibondarenko.clavionx.dto.student;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for detailed student view with courses and statistics
 */
public record StudentDetailDto(
    Long id,
    String name,
    String lastName,
    String fullName,
    String username,
    String email,
    String phoneNumber,
    String studentNumber,
    LocalDate enrollmentDate,
    Integer graduationYear,
    boolean enabled,
    List<CourseBriefDto> courses,
    int tasksCount,
    String avatarPath
) {
    
    public static StudentDetailDto from(io.github.vibondarenko.clavionx.entity.Student student) {
        List<CourseBriefDto> courseDtos = student.getCourses().stream()
            .map(CourseBriefDto::from)
            .toList();
        
        return new StudentDetailDto(
            student.getId(),
            student.getName(),
            student.getLastName(),
            student.getName() + " " + student.getLastName(),
            student.getUsername(),
            student.getEmail(),
            student.getPhoneNumber(),
            student.getStudentNumber(),
            student.getEnrollmentDate(),
            student.getGraduationYear(),
            student.isEnabled(),
            courseDtos,
            student.getTasks().size(),
            student.getAvatarPath()
        );
    }
}