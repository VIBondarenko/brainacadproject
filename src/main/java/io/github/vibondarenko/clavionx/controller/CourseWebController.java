package io.github.vibondarenko.clavionx.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.vibondarenko.clavionx.entity.Course;
import io.github.vibondarenko.clavionx.repository.CourseRepository;
import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.service.UserActivityService;
import io.github.vibondarenko.clavionx.view.ViewAttributes;

@Controller
@RequestMapping("/courses")
public class CourseWebController {

	private final CourseRepository courseRepository;
	private final UserActivityService activityService;

	public CourseWebController(CourseRepository courseRepository, UserActivityService activityService) {
		this.courseRepository = courseRepository;
		this.activityService = activityService;
	}

	/**
	 * Show list of all courses
	 */
	@GetMapping("")
	public String listCourses(Model model) {
		try {
            model.addAttribute(ViewAttributes.PAGE_TITLE, "Courses");
            model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Manage your courses effectively");
            model.addAttribute(ViewAttributes.PAGE_ICON, "fa-graduation-cap");
			model.addAttribute("courses", courseRepository.findAll());
			
			// Log activity
			activityService.logActivity(
				UserActivityService.ActivityType.COURSE_VIEW, 
				"Viewed courses list"
			);
			
			return "courses/list";
		} catch (Exception e) {
			activityService.logFailedActivity(
				UserActivityService.ActivityType.COURSE_VIEW,
				"Failed to load courses list",
				e.getMessage()
			);
			throw e;
		}
	}

	/**
	 * Show form to create a new course
	 */
	@GetMapping("/new")
	public String showCreateForm(Model model) {
		Course course = new Course("", "");

		model.addAttribute(ViewAttributes.PAGE_TITLE, "New Course");
		model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Add a new course to the system");
		model.addAttribute(ViewAttributes.PAGE_ICON, "fa-plus-circle");

		model.addAttribute("course", course);
		model.addAttribute("isEdit", false);
		
		// Format dates for the form
		model.addAttribute("beginDateStr", course.getBeginDate().toString());
		model.addAttribute("endDateStr", course.getEndDate().toString());
		
		return "courses/form";
	}
	
	/**
	 * Create a new course
	 */
	@PostMapping("/new")
	public String createCourse(@ModelAttribute Course course, 
								@RequestParam(required = false) String beginDate,
								@RequestParam(required = false) String endDate,
								RedirectAttributes redirectAttributes) {
		try {
			// Parse dates if provided
			if (beginDate != null && !beginDate.isEmpty()) {
				course.setBeginDate(LocalDate.parse(beginDate));
			}
			if (endDate != null && !endDate.isEmpty()) {
				course.setEndDate(LocalDate.parse(endDate));
			}
			
			// Save the course
			Course savedCourse = courseRepository.save(course);
			
			// Log activity
			activityService.logActivity(
				UserActivityService.ActivityType.COURSE_CREATE,
				"Created new course: " + course.getName(),
				"Course",
				savedCourse.getId()
			);
			
			redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
			return Paths.REDIRECT_COURSES;
			
		} catch (DateTimeParseException e) {
			activityService.logFailedActivity(
				UserActivityService.ActivityType.COURSE_CREATE,
				"Failed to create course - invalid date format",
				e.getMessage()
			);
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
			return Paths.REDIRECT_COURSES_NEW;
		} catch (Exception e) {
			activityService.logFailedActivity(
				UserActivityService.ActivityType.COURSE_CREATE,
				"Failed to create course: " + course.getName(),
				e.getMessage()
			);
			redirectAttributes.addFlashAttribute("errorMessage", "Error creating course: " + e.getMessage());
			return Paths.REDIRECT_COURSES_NEW;
		}
	}
	
	/**
	 * Show form to edit existing course
	 */
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		Course course = courseRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Course not found"));

		model.addAttribute(ViewAttributes.PAGE_TITLE, "Edit Course");
		model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Edit course details");
		model.addAttribute(ViewAttributes.PAGE_ICON, "fa-edit");

		model.addAttribute("course", course);
		model.addAttribute("isEdit", true);
		
		// Format dates for the form
		model.addAttribute("beginDateStr", course.getBeginDate().toString());
		model.addAttribute("endDateStr", course.getEndDate().toString());
		
		return "courses/form";
	}
	
	/**
	 * Update existing course
	 */
	@PostMapping("/{id}/edit")
	public String updateCourse(@PathVariable Long id,
								@ModelAttribute Course course,
								@RequestParam(required = false) String beginDate,
								@RequestParam(required = false) String endDate,
								RedirectAttributes redirectAttributes) {
		try {
			Course existingCourse = courseRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Course not found"));
			
			// Update fields
			existingCourse.setName(course.getName());
			existingCourse.setDescription(course.getDescription());
			
			// Parse dates if provided
			if (beginDate != null && !beginDate.isEmpty()) {
				existingCourse.setBeginDate(LocalDate.parse(beginDate));
			}
			if (endDate != null && !endDate.isEmpty()) {
				existingCourse.setEndDate(LocalDate.parse(endDate));
			}
			
			// Save the course
			courseRepository.save(existingCourse);
			redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
			return Paths.REDIRECT_COURSES;
			
		} catch (DateTimeParseException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
			return Paths.REDIRECT_COURSES_EDIT.formatted(id);
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error updating course: " + e.getMessage());
			return Paths.REDIRECT_COURSES_EDIT.formatted(id);
		}
	}
	
	/**
	 * Show course details
	 */
	@GetMapping("/{id}")
	public String viewCourse(@PathVariable Long id, Model model) {
		Course course = courseRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Course not found"));
			
		// Log activity
		activityService.logActivity(
			UserActivityService.ActivityType.COURSE_VIEW,
			"Viewed course details: " + course.getName(),
			"Course",
			id
		);
		model.addAttribute(ViewAttributes.PAGE_TITLE, course.getName());
		model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, course.getDescription());
		model.addAttribute(ViewAttributes.PAGE_ICON, "fa-graduation-cap");

		model.addAttribute("course", course);
		
		// Add formatted dates
		if (course.getBeginDate() != null) {
			model.addAttribute("beginDateStr", course.getBeginDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		} else {
			model.addAttribute("beginDateStr", "Not set");
		}
		
		if (course.getEndDate() != null) {
			model.addAttribute("endDateStr", course.getEndDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		} else {
			model.addAttribute("endDateStr", "Not set");
		}
		
		// Add empty collections for now (to prevent template errors)
		model.addAttribute("students", new ArrayList<>());
		model.addAttribute("allStudents", new ArrayList<>());
		model.addAttribute("trainer", null);
		model.addAttribute("availablePlaces", 20); // default value
		
		return "courses/view";
	}
	
	/**
	 * Delete course
	 */
	@PostMapping("/{id}/delete")
	public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			Course course = courseRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Course not found"));
			
			String courseName = course.getName();
			courseRepository.delete(course);
			
			// Log activity
			activityService.logActivity(
				UserActivityService.ActivityType.COURSE_DELETE,
				"Deleted course: " + courseName,
				"Course",
				id
			);
			
			redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
			return Paths.REDIRECT_COURSES;
			
		} catch (Exception e) {
			activityService.logFailedActivity(
				UserActivityService.ActivityType.COURSE_DELETE,
				"Failed to delete course with ID: " + id,
				e.getMessage()
			);
			redirectAttributes.addFlashAttribute("errorMessage", "Error deleting course: " + e.getMessage());
			return Paths.REDIRECT_COURSES;
		}
	}
}



