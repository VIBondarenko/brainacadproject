package com.brainacad.ecs.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.brainacad.ecs.entity.Course;
import com.brainacad.ecs.repository.CourseRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST API Controller for Course Management
 * Provides RESTful endpoints for course operations with comprehensive documentation
 */
@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "Operations for managing courses")
@SecurityRequirement(name = "session-auth")
public class CourseApiController {

    @Autowired
    private CourseRepository courseRepository;

    @Operation(
            summary = "Get all courses",
            description = "Retrieve a paginated list of all courses. Supports filtering and sorting."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved courses",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content)
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Course>> getAllCourses(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        
        List<Course> courses = courseRepository.findAll();
        return ResponseEntity.ok(courses);
    }

    @Operation(
            summary = "Get course by ID",
            description = "Retrieve a specific course by its unique identifier"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<Course> getCourseById(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id) {
        
        Optional<Course> course = courseRepository.findById(id);
        return course.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create new course",
            description = "Create a new course. Requires TEACHER role or higher."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "400", description = "Invalid course data",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions",
                    content = @Content)
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<Course> createCourse(
            @Parameter(description = "Course details", required = true)
            @RequestBody Course course) {
        
        try {
            Course savedCourse = courseRepository.save(course);
            return ResponseEntity.status(201).body(savedCourse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(
            summary = "Update existing course",
            description = "Update an existing course. Requires TEACHER role or higher."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class))),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions",
                    content = @Content)
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<Course> updateCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated course details", required = true)
            @RequestBody Course course) {
        
        return courseRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setName(course.getName());
                    existingCourse.setDescription(course.getDescription());
                    existingCourse.setBeginDate(course.getBeginDate());
                    existingCourse.setEndDate(course.getEndDate());
                    Course updatedCourse = courseRepository.save(existingCourse);
                    return ResponseEntity.ok(updatedCourse);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete course",
            description = "Delete a course by ID. Requires ADMIN role or higher."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Course not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Insufficient permissions",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID", required = true, example = "1")
            @PathVariable Long id) {
        
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(
            summary = "Search courses",
            description = "Search courses by name or description"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Course.class)))
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Course>> searchCourses(
            @Parameter(description = "Search query", required = true, example = "Java")
            @RequestParam String query) {
        
        // For now, simple filter by name containing query
        List<Course> courses = courseRepository.findAll().stream()
                .filter(course -> course.getName().toLowerCase().contains(query.toLowerCase()) ||
                                course.getDescription().toLowerCase().contains(query.toLowerCase()))
                .toList();
        
        return ResponseEntity.ok(courses);
    }
}
