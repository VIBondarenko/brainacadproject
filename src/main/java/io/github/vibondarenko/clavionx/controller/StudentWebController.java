package io.github.vibondarenko.clavionx.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.vibondarenko.clavionx.dto.student.CreateStudentRequest;
import io.github.vibondarenko.clavionx.dto.student.StudentDetailDto;
import io.github.vibondarenko.clavionx.dto.student.StudentListDto;
import io.github.vibondarenko.clavionx.dto.student.StudentSearchCriteria;
import io.github.vibondarenko.clavionx.dto.student.UpdateStudentRequest;
import io.github.vibondarenko.clavionx.service.StudentService;
import io.github.vibondarenko.clavionx.service.UserActivityService;
import io.github.vibondarenko.clavionx.view.ViewAttributes;
import jakarta.validation.Valid;

/**
 * Web controller for managing students (list/create/edit/view/delete & enrollment placeholders).
 */
@Controller
@RequestMapping("/students")
public class StudentWebController {

    private static final Logger log = LoggerFactory.getLogger(StudentWebController.class);

    private static final String VIEW_LIST = "students/list";
    private static final String VIEW_FORM = "students/form";
    private static final String VIEW_VIEW = "students/view";
    private static final String REDIRECT_STUDENTS = "redirect:/students";
    private static final String REDIRECT_STUDENT_PREFIX = "redirect:/students/";

    private final StudentService studentService;
    private final UserActivityService activityService;

    public StudentWebController(StudentService studentService, UserActivityService activityService) {
        this.studentService = studentService;
        this.activityService = activityService;
    }

    /**
     * List students with simple search + pagination.
     */
    @GetMapping("")
    public String listStudents(@RequestParam(value = "q", required = false) String query,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "20") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100));
    StudentSearchCriteria criteria = new StudentSearchCriteria(query, null, null, null, null, null);
        Page<StudentListDto> resultPage = studentService.searchStudents(criteria, pageable);

        model.addAttribute(ViewAttributes.PAGE_TITLE, "Students");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Manage platform students");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user-graduate");
        model.addAttribute("studentsPage", resultPage);
        model.addAttribute("query", query);

        activityService.logActivity(UserActivityService.ActivityType.STUDENT_VIEW, "Viewed students list");
    return VIEW_LIST; // template to be created
    }

    /** Show create form */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "New Student");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Register a new student");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user-plus");
    model.addAttribute("createRequest", new CreateStudentRequest("", "", "", "", "", 2025));
    return VIEW_FORM; // template to be created
    }

    /** Create student */
    @PostMapping("/new")
    public String createStudent(@ModelAttribute("createRequest") @Valid CreateStudentRequest request,
                                BindingResult binding,
                                RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return VIEW_FORM;
        }
        try {
            StudentDetailDto dto = studentService.createStudent(request);
            redirectAttributes.addFlashAttribute(ViewAttributes.SUCCESS_MESSAGE, "Student created: " + dto.username());
            activityService.logActivity(UserActivityService.ActivityType.STUDENT_CREATE, "Created student " + dto.username(), "Student", dto.id());
            return REDIRECT_STUDENTS;
        } catch (Exception e) {
            log.error("Failed to create student", e);
            redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, e.getMessage());
            activityService.logFailedActivity(UserActivityService.ActivityType.STUDENT_CREATE, "Failed to create student", e.getMessage());
            return REDIRECT_STUDENTS + "/new";
        }
    }

    /** View student details */
    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return studentService.getStudent(id)
            .map(dto -> {
                model.addAttribute(ViewAttributes.PAGE_TITLE, dto.fullName());
                model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Student details");
                model.addAttribute(ViewAttributes.PAGE_ICON, "fa-id-card");
                model.addAttribute("student", dto);
                // Provide enrolled and available courses for UI
                model.addAttribute("enrolledCourses", studentService.getEnrolledCourses(id));
                model.addAttribute("availableCourses", studentService.getAvailableCourses(id));
                activityService.logActivity(UserActivityService.ActivityType.STUDENT_VIEW, "Viewed student " + dto.username(), "Student", dto.id());
                return VIEW_VIEW; // template to be created
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, "Student not found");
                return REDIRECT_STUDENTS;
            });
    }

    /** Show edit form */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return studentService.getStudent(id)
            .map(dto -> {
                model.addAttribute(ViewAttributes.PAGE_TITLE, "Edit: " + dto.fullName());
                model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Update student information");
                model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user-pen");
                Integer gradYear = dto.graduationDate() != null ? dto.graduationDate().getYear() : null;
                model.addAttribute("updateRequest", new UpdateStudentRequest(dto.name(), dto.lastName(), dto.email(), dto.phoneNumber(), gradYear));
                model.addAttribute("studentId", dto.id());
                return VIEW_FORM; // reuse form template with conditional fields
            })
            .orElseGet(() -> {
                redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, "Student not found");
                return REDIRECT_STUDENTS;
            });
    }

    /** Update student */
    @PostMapping("/{id}/edit")
    public String updateStudent(@PathVariable Long id,
                                @ModelAttribute("updateRequest") @Valid UpdateStudentRequest request,
                                BindingResult binding,
                                RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return VIEW_FORM;
        }
        try {
            StudentDetailDto dto = studentService.updateStudent(id, request);
            redirectAttributes.addFlashAttribute(ViewAttributes.SUCCESS_MESSAGE, "Student updated: " + dto.username());
            activityService.logActivity(UserActivityService.ActivityType.STUDENT_UPDATE, "Updated student " + dto.username(), "Student", dto.id());
            return REDIRECT_STUDENT_PREFIX + id;
        } catch (Exception e) {
            log.error("Failed to update student {}", id, e);
            redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, e.getMessage());
            activityService.logFailedActivity(UserActivityService.ActivityType.STUDENT_UPDATE, "Failed to update student id=" + id, e.getMessage());
            return REDIRECT_STUDENT_PREFIX + id + "/edit";
        }
    }

    /** Delete student */
    @PostMapping("/{id}/delete")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute(ViewAttributes.SUCCESS_MESSAGE, "Student deleted");
            activityService.logActivity(UserActivityService.ActivityType.STUDENT_DELETE, "Deleted student id=" + id, "Student", id);
        } catch (Exception e) {
            log.error("Failed to delete student {}", id, e);
            redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, e.getMessage());
            activityService.logFailedActivity(UserActivityService.ActivityType.STUDENT_DELETE, "Failed to delete student id=" + id, e.getMessage());
        }
    return REDIRECT_STUDENTS;
    }

    // Enrollment endpoints placeholders (will be refined when course integration step implemented)
    @PostMapping("/{id}/enroll/{courseId}")
    public String enroll(@PathVariable Long id, @PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        try {
            studentService.enrollStudentInCourse(id, courseId);
            redirectAttributes.addFlashAttribute(ViewAttributes.SUCCESS_MESSAGE, "Student enrolled in course");
            activityService.logActivity(UserActivityService.ActivityType.STUDENT_ENROLL, "Enrolled student id=" + id + " in course " + courseId, "Student", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, e.getMessage());
            activityService.logFailedActivity(UserActivityService.ActivityType.STUDENT_ENROLL, "Failed enrollment student id=" + id + " course=" + courseId, e.getMessage());
        }
    return REDIRECT_STUDENT_PREFIX + id;
    }

    @PostMapping("/{id}/unenroll/{courseId}")
    public String unenroll(@PathVariable Long id, @PathVariable Long courseId, RedirectAttributes redirectAttributes) {
        try {
            studentService.unenrollStudentFromCourse(id, courseId);
            redirectAttributes.addFlashAttribute(ViewAttributes.SUCCESS_MESSAGE, "Student unenrolled from course");
            activityService.logActivity(UserActivityService.ActivityType.STUDENT_UNENROLL, "Unenrolled student id=" + id + " from course " + courseId, "Student", id);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(ViewAttributes.ERROR_MESSAGE, e.getMessage());
            activityService.logFailedActivity(UserActivityService.ActivityType.STUDENT_UNENROLL, "Failed unenrollment student id=" + id + " course=" + courseId, e.getMessage());
        }
        return REDIRECT_STUDENT_PREFIX + id;
    }
}
