package com.brainacad.ecs.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.brainacad.ecs.entity.Student;
import com.brainacad.ecs.entity.Trainer;
import com.brainacad.ecs.entity.Course;
import com.brainacad.ecs.facade.EducationSystemFacade;

/**
 * Course Management Web Controller
 * Handles all course-related web operations with full Spring MVC support
 */
@Controller
@RequestMapping("/courses")
public class CourseWebController {

    @Autowired
    private EducationSystemFacade educationSystemFacade;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  // ISO format for HTML date input
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy");  // Display format

    /**
     * Display list of all courses
     */
    @GetMapping
    public String listCourses(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        
        System.out.println("🔄 WORKING: CourseWebController.listCourses() called with search=" + search + ", page=" + page);
        
        try {
            System.out.println("Getting all courses from facade...");
            List<Course> courses = educationSystemFacade.getAllCourses();
            System.out.println("Retrieved courses count: " + (courses != null ? courses.size() : "null"));
            if (courses != null) {
                courses.forEach(course -> System.out.println("Course: " + course.getName()));
            } else {
                System.out.println("Courses list is NULL!");
                courses = List.of(); // Create empty list
            }
            
            // Simple search implementation
            if (search != null && !search.trim().isEmpty()) {
                System.out.println("Filtering courses by search: " + search);
                courses = courses.stream()
                    .filter(course -> course.getName().toLowerCase().contains(search.toLowerCase()) ||
                                    course.getDescription().toLowerCase().contains(search.toLowerCase()))
                    .toList();
                model.addAttribute("search", search);
                System.out.println("Filtered courses count: " + (courses != null ? courses.size() : "null"));
            }
            
            // Simple pagination
            int totalCourses = courses.size();
            System.out.println("Total courses for pagination: " + totalCourses);
            int totalPages = (int) Math.ceil((double) totalCourses / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalCourses);
            
            System.out.println("Pagination - startIndex: " + startIndex + ", endIndex: " + endIndex);
            List<Course> paginatedCourses = courses.subList(startIndex, endIndex);
            
            System.out.println("Paginated courses count: " + paginatedCourses.size());
            System.out.println("Adding to model - courses size: " + paginatedCourses.size());
            
            model.addAttribute("courses", paginatedCourses);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalCourses", totalCourses);
            
            System.out.println("All attributes added to model successfully");
            System.out.println("Returning template: courses/list");
            return "courses/list";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading courses: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show course details
     */
    @GetMapping("/{id}")
    public String showCourse(@PathVariable int id, Model model) {
        try {
            Course course = educationSystemFacade.findCourseById(id);
            if (course == null) {
                model.addAttribute("error", "Course not found");
                return "error";
            }
            
            model.addAttribute("course", course);
            model.addAttribute("students", course.getStudents());
            model.addAttribute("trainer", course.getTrainer());
            model.addAttribute("availablePlaces", course.getCountPlaces());
            model.addAttribute("allStudents", educationSystemFacade.getAllStudents());
            model.addAttribute("beginDateStr", dateFormat.format(course.getBeginDate()));  // ISO format for form inputs
            model.addAttribute("endDateStr", dateFormat.format(course.getEndDate()));  // ISO format for form inputs
            
            return "courses/view";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading course: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show create course form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("trainers", educationSystemFacade.getAllTrainers());
        model.addAttribute("isEdit", false);  // Explicitly set isEdit to false
        return "courses/form";
    }

    /**
     * Handle course creation
     */
    @PostMapping("/new")
    public String createCourse(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "beginDate", required = false) String beginDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "days", defaultValue = "") String days,
            @RequestParam(value = "trainerId", required = false) Integer trainerId,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("🔄 Creating course with:");
        System.out.println("  Name: " + name);
        System.out.println("  Description: " + description);
        System.out.println("  Begin Date String: '" + beginDateStr + "'");
        System.out.println("  End Date String: '" + endDateStr + "'");
        System.out.println("  Days: " + days);
        System.out.println("  Trainer ID: " + trainerId);
        
        try {
            Date beginDate = new Date();
            Date endDate = new Date();
            
            if (beginDateStr != null && !beginDateStr.isEmpty()) {
                try {
                    beginDate = dateFormat.parse(beginDateStr);
                    System.out.println("✅ Begin date parsed successfully: " + beginDate);
                } catch (Exception e) {
                    System.out.println("❌ Error parsing begin date: " + e.getMessage());
                    // Try alternative formats
                    try {
                        SimpleDateFormat altFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH);
                        beginDate = altFormat.parse(beginDateStr);
                        System.out.println("✅ Begin date parsed with alternative format: " + beginDate);
                    } catch (Exception e2) {
                        System.out.println("❌ Failed to parse begin date with alternative format: " + e2.getMessage());
                        beginDate = new Date();
                    }
                }
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = dateFormat.parse(endDateStr);
                    System.out.println("✅ End date parsed successfully: " + endDate);
                } catch (Exception e) {
                    System.out.println("❌ Error parsing end date: " + e.getMessage());
                    // Try alternative formats
                    try {
                        SimpleDateFormat altFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.ENGLISH);
                        endDate = altFormat.parse(endDateStr);
                        System.out.println("✅ End date parsed with alternative format: " + endDate);
                    } catch (Exception e2) {
                        System.out.println("❌ Failed to parse end date with alternative format: " + e2.getMessage());
                        endDate = new Date();
                    }
                }
            }
            
            Course course = new Course(name, description, beginDate, endDate, days);
            
            if (trainerId != null) {
                Trainer trainer = educationSystemFacade.findTrainerById(trainerId);
                if (trainer != null) {
                    course.setTrainer(trainer);
                    System.out.println("✅ Trainer assigned: " + trainer.getName());
                } else {
                    System.out.println("❌ Trainer not found with ID: " + trainerId);
                }
            }
            
            educationSystemFacade.addCourse(course);
            System.out.println("✅ Course created successfully: " + course.getName());
            redirectAttributes.addFlashAttribute("success", "Course successfully created!");
            return "redirect:/courses";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating course: " + e.getMessage());
            return "redirect:/courses/create";
        }
    }

    /**
     * Show edit course form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable int id, Model model) {
        try {
            Course course = educationSystemFacade.findCourseById(id);
            if (course == null) {
                model.addAttribute("error", "Course not found");
                return "error";
            }
            
            model.addAttribute("course", course);
            model.addAttribute("trainers", educationSystemFacade.getAllTrainers());
            model.addAttribute("beginDateStr", dateFormat.format(course.getBeginDate()));  // ISO format for form inputs
            model.addAttribute("endDateStr", dateFormat.format(course.getEndDate()));  // ISO format for form inputs
            model.addAttribute("isEdit", true);
            
            return "courses/form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading course: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Handle course update
     */
    @PostMapping("/{id}/edit")
    public String updateCourse(
            @PathVariable int id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "beginDate", required = false) String beginDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "days", defaultValue = "") String days,
            @RequestParam(value = "trainerId", required = false) Integer trainerId,
            RedirectAttributes redirectAttributes) {
        
        try {
            Course course = educationSystemFacade.findCourseById(id);
            if (course == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found");
                return "redirect:/courses";
            }
            
            course.setName(name);
            course.setDescription(description);
            
            if (beginDateStr != null && !beginDateStr.isEmpty()) {
                course.getBeginDate().setTime(dateFormat.parse(beginDateStr).getTime());
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                course.getEndDate().setTime(dateFormat.parse(endDateStr).getTime());
            }
            
            if (trainerId != null) {
                Trainer trainer = educationSystemFacade.findTrainerById(trainerId);
                course.setTrainer(trainer);
            } else {
                course.deleteTrainer();
            }
            
            redirectAttributes.addFlashAttribute("success", "Course successfully updated!");
            return "redirect:/courses/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating course: " + e.getMessage());
            return "redirect:/courses/" + id + "/edit";
        }
    }

    /**
     * Delete course
     */
    @PostMapping("/{id}/delete")
    public String deleteCourse(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            Course course = educationSystemFacade.findCourseById(id);
            if (course == null) {
                redirectAttributes.addFlashAttribute("error", "Course not found");
                return "redirect:/courses";
            }
            
            educationSystemFacade.removeCourse(course);
            redirectAttributes.addFlashAttribute("success", "Course successfully deleted!");
            return "redirect:/courses";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting course: " + e.getMessage());
            return "redirect:/courses";
        }
    }

    /**
     * Add student to course
     */
    @PostMapping("/{courseId}/students/add")
    public String addStudentToCourse(
            @PathVariable int courseId, 
            @RequestParam int studentId,
            RedirectAttributes redirectAttributes) {
        
        try {
            Course course = educationSystemFacade.findCourseById(courseId);
            Student student = educationSystemFacade.findStudentById(studentId);
            
            if (course == null || student == null) {
                redirectAttributes.addFlashAttribute("error", "Course or student not found");
                return "redirect:/courses/" + courseId;
            }
            
            if (course.addStudent(student)) {
                redirectAttributes.addFlashAttribute("success", "Student successfully added to course!");
            } else {
                redirectAttributes.addFlashAttribute("error", "No available seats in course");
            }
            
            return "redirect:/courses/" + courseId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding student: " + e.getMessage());
            return "redirect:/courses/" + courseId;
        }
    }

    /**
     * Remove student from course
     */
    @PostMapping("/{courseId}/students/{studentId}/remove")
    public String removeStudentFromCourse(
            @PathVariable int courseId, 
            @PathVariable int studentId,
            RedirectAttributes redirectAttributes) {
        
        try {
            Course course = educationSystemFacade.findCourseById(courseId);
            Student student = educationSystemFacade.findStudentById(studentId);
            
            if (course == null || student == null) {
                redirectAttributes.addFlashAttribute("error", "Course or student not found");
                return "redirect:/courses/" + courseId;
            }
            
            course.deleteStudent(student);
            redirectAttributes.addFlashAttribute("success", "Student successfully removed from course!");
            
            return "redirect:/courses/" + courseId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error removing student: " + e.getMessage());
            return "redirect:/courses/" + courseId;
        }
    }
}
