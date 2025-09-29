package io.github.vibondarenko.clavionx.dto.student;

import java.time.LocalDate;

/**
 * Brief course information for student detail view
 */
public record CourseBriefDto(
    Long id,
    String name,
    String description,
    LocalDate beginDate,
    LocalDate endDate,
    boolean isActive,
    String status
) {
    
    public static CourseBriefDto from(io.github.vibondarenko.clavionx.entity.Course course) {
        boolean active = course.isActive();
        String status = course.isUpcoming() ? "Upcoming" : 
                        course.isCompleted() ? "Completed" : "Active";
        
        return new CourseBriefDto(
            course.getId(),
            course.getName(),
            course.getDescription(),
            course.getBeginDate(),
            course.getEndDate(),
            active,
            status
        );
    }
}