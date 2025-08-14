package com.brainacad.ecs.controller.web;

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

import com.brainacad.ecs.Course;
import com.brainacad.ecs.Student;
import com.brainacad.ecs.Trainer;
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
                courses = List.of(); // Создаем пустой список
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
            model.addAttribute("error", "Ошибка загрузки курсов: " + e.getMessage());
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
                model.addAttribute("error", "Курс не найден");
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
            model.addAttribute("error", "Ошибка загрузки курса: " + e.getMessage());
            return "error";
        }
    }

    /**
     * Show create course form
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("trainers", educationSystemFacade.getAllTrainers());
        model.addAttribute("isEdit", false);  // Explicitly set isEdit to false
        return "courses/form";
    }

    /**
     * Handle course creation
     */
    @PostMapping("/create")
    public String createCourse(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam(value = "beginDate", required = false) String beginDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @RequestParam(value = "days", defaultValue = "") String days,
            @RequestParam(value = "trainerId", required = false) Integer trainerId,
            RedirectAttributes redirectAttributes) {
        
        try {
            Date beginDate = new Date();
            Date endDate = new Date();
            
            if (beginDateStr != null && !beginDateStr.isEmpty()) {
                beginDate = dateFormat.parse(beginDateStr);
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
            }
            
            Course course = new Course(name, description, beginDate, endDate, days);
            
            if (trainerId != null) {
                Trainer trainer = educationSystemFacade.findTrainerById(trainerId);
                if (trainer != null) {
                    course.setTrainer(trainer);
                }
            }
            
            educationSystemFacade.addCourse(course);
            redirectAttributes.addFlashAttribute("success", "Курс успешно создан!");
            return "redirect:/courses";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка создания курса: " + e.getMessage());
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
                model.addAttribute("error", "Курс не найден");
                return "error";
            }
            
            model.addAttribute("course", course);
            model.addAttribute("trainers", educationSystemFacade.getAllTrainers());
            model.addAttribute("beginDateStr", dateFormat.format(course.getBeginDate()));  // ISO format for form inputs
            model.addAttribute("endDateStr", dateFormat.format(course.getEndDate()));  // ISO format for form inputs
            model.addAttribute("isEdit", true);
            
            return "courses/form";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки курса: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Курс не найден");
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
            
            redirectAttributes.addFlashAttribute("success", "Курс успешно обновлен!");
            return "redirect:/courses/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления курса: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Курс не найден");
                return "redirect:/courses";
            }
            
            educationSystemFacade.removeCourse(course);
            redirectAttributes.addFlashAttribute("success", "Курс успешно удален!");
            return "redirect:/courses";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления курса: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Курс или студент не найден");
                return "redirect:/courses/" + courseId;
            }
            
            if (course.addStudent(student)) {
                redirectAttributes.addFlashAttribute("success", "Студент успешно добавлен в курс!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Нет свободных мест в курсе");
            }
            
            return "redirect:/courses/" + courseId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка добавления студента: " + e.getMessage());
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
                redirectAttributes.addFlashAttribute("error", "Курс или студент не найден");
                return "redirect:/courses/" + courseId;
            }
            
            course.deleteStudent(student);
            redirectAttributes.addFlashAttribute("success", "Студент успешно удален из курса!");
            
            return "redirect:/courses/" + courseId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления студента: " + e.getMessage());
            return "redirect:/courses/" + courseId;
        }
    }
}
