package io.github.vibondarenko.clavionx.dto.student;

import java.time.LocalDate;

/**
 * Search criteria for filtering students
 */
public record StudentSearchCriteria(
    String query, // Search in name, lastName, email, username, studentNumber
    Long courseId, // Students enrolled in specific course
    Integer graduationYear,
    LocalDate enrollmentFrom,
    LocalDate enrollmentTo,
    Boolean enabled
) {
    
    public boolean hasQuery() {
        return query != null && !query.trim().isEmpty();
    }
    
    public boolean hasCourseFilter() {
        return courseId != null;
    }
    
    public boolean hasGraduationYearFilter() {
        return graduationYear != null;
    }
    
    public boolean hasEnrollmentDateFilter() {
        return enrollmentFrom != null || enrollmentTo != null;
    }
    
    public boolean hasEnabledFilter() {
        return enabled != null;
    }
}