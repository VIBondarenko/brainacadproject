package com.brainacad.ecs.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brainacad.ecs.entity.Course;
import com.brainacad.ecs.repository.CourseRepository;

@Controller
@RequestMapping("/courses")
public class CourseWebController {

	private final CourseRepository courseRepository;

	public CourseWebController(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	/**
	 * Show list of all courses
	 */
	@GetMapping("")
	public String listCourses(Model model) {
		System.out.println("=== CourseWebController.listCourses() called ===");
		try {
			model.addAttribute("courses", courseRepository.findAll());
			System.out.println("=== Courses loaded successfully ===");
			return "courses/list";
		} catch (Exception e) {
			System.out.println("=== Error loading courses: " + e.getMessage() + " ===");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Show form to create a new course
	 */
	@GetMapping("/new")
	public String showCreateForm(Model model) {
		Course course = new Course("", "");
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
			courseRepository.save(course);
			redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
			return "redirect:/courses";
			
		} catch (DateTimeParseException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
			return "redirect:/courses/new";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error creating course: " + e.getMessage());
			return "redirect:/courses/new";
		}
	}
	
	/**
	 * Show form to edit existing course
	 */
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Long id, Model model) {
		Course course = courseRepository.findById(id)
			.orElseThrow(() -> new RuntimeException("Course not found"));
			
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
			return "redirect:/courses";
			
		} catch (DateTimeParseException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Invalid date format");
			return "redirect:/courses/" + id + "/edit";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error updating course: " + e.getMessage());
			return "redirect:/courses/" + id + "/edit";
		}
	}
}
