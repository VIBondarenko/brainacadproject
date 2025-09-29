package io.github.vibondarenko.clavionx.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import io.github.vibondarenko.clavionx.dto.student.CreateStudentRequest;
import io.github.vibondarenko.clavionx.entity.Course;
import io.github.vibondarenko.clavionx.repository.CourseRepository;
import io.github.vibondarenko.clavionx.service.StudentService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentViewControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseRepository courseRepository;

    private Long studentId;

    @BeforeEach
    void init() {
        if (studentId == null) {
            var dto = studentService.createStudent(new CreateStudentRequest("MVC","Student","mvc@test.dev",null,null,2027));
            studentId = dto.id();
            var c = courseRepository.save(new Course("MVC Course","desc", LocalDate.now(), LocalDate.now().plusMonths(1)));
            studentService.enrollStudentInCourse(studentId, c.getId());
        }
    }

    @Test
    @DisplayName("Страница студента содержит enrolledCourses и availableCourses")
    void viewStudentPage() throws Exception {
        mockMvc.perform(get("/students/" + studentId))
            .andExpect(status().isOk())
            .andExpect(view().name("students/view"))
            .andExpect(model().attributeExists("student"))
            .andExpect(model().attributeExists("enrolledCourses"))
            .andExpect(model().attributeExists("availableCourses"));
    }
}
