package io.github.vibondarenko.clavionx.controller.api;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.vibondarenko.clavionx.entity.Course;
import io.github.vibondarenko.clavionx.repository.CourseRepository;
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
 * and security annotations.
 */
@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Course Management", description = "Operations for managing courses")
@SecurityRequirement(name = "session-auth")
public class CourseApiController {

	private final CourseRepository courseRepository;

	/**
	 * Constructor for CourseApiController
	 * 
	 * @param courseRepository Repository for Course entities
	 */
	public CourseApiController(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	/**
	 * Get all courses with pagination, filtering, and sorting support.
	 * Accessible by users with STUDENT role or higher.
	 * 
	 * @param pageable Pagination parameters
	 * @return Paginated list of courses
	 */
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
	@io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations.StudentArea
	public ResponseEntity<List<Course>> getAllCourses(
			@Parameter(description = "Pagination parameters") Pageable pageable) {
		
		List<Course> courses = courseRepository.findAll();
		return ResponseEntity.ok(courses);
	}

	/**
	 * Get a specific course by its ID.
	 * Accessible by users with STUDENT role or higher.
	 * 
	 * @param id Course ID
	 * @return Course details or 404 if not found
	 */
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
	@io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations.StudentArea
	public ResponseEntity<Course> getCourseById(
			@Parameter(description = "Course ID", required = true, example = "1")
			@PathVariable Long id) {
		
		Optional<Course> course = courseRepository.findById(id);
		return course.map(ResponseEntity::ok)
					.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Create a new course.
	 * Accessible by users with TEACHER role or higher.
	 * 
	 * @param course Course details
	 * @return Created course with 201 status or 400 if invalid
	 */
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
	@io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations.CourseManage
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
	
	/**
	 * Update an existing course by its ID.
	 * Accessible by users with TEACHER role or higher.
	 * 
	 * @param id Course ID
	 * @param course Updated course details
	 * @return Updated course or 404 if not found
	 */
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
	@io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations.CourseManage
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

	/**
	 * Delete a course by its ID.
	 * Accessible by users with ADMIN role or higher.
	 * 
	 * @param id Course ID
	 * @return 204 if deleted or 404 if not found
	 */
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
	@io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations.AdminOrManager
	public ResponseEntity<Void> deleteCourse(
			@Parameter(description = "Course ID", required = true, example = "1")
			@PathVariable Long id) {
		
		if (courseRepository.existsById(id)) {
			courseRepository.deleteById(id);
				return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	/**
	 * Search courses by name or description.
	 * Accessible by users with STUDENT role or higher.
	 * 
	 * @param query Search query
	 * @return List of matching courses
	 */
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
	@io.github.vibondarenko.clavionx.security.annotations.SecurityAnnotations.StudentArea
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