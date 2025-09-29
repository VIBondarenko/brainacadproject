package io.github.vibondarenko.clavionx.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.vibondarenko.clavionx.dto.student.CourseBriefDto;
import io.github.vibondarenko.clavionx.dto.student.CreateStudentRequest;
import io.github.vibondarenko.clavionx.entity.Course;
import io.github.vibondarenko.clavionx.repository.CourseRepository;

@SpringBootTest
@ActiveProfiles("test")
class StudentCourseServiceTest {

    @Autowired
    private StudentService studentService;
    @Autowired
    private CourseRepository courseRepository;

    private Long studentId;
    private Long courseA;
    private Long courseB;

    @BeforeEach
    void setup() {
        if (studentId == null) {
            var dto = studentService.createStudent(new CreateStudentRequest("Test","User","t@test.dev",null,null,2026));
            studentId = dto.id();
            courseA = courseRepository.save(new Course("Course A","desc", LocalDate.now(), LocalDate.now().plusMonths(1))).getId();
            courseB = courseRepository.save(new Course("Course B","desc", LocalDate.now(), LocalDate.now().plusMonths(1))).getId();
        }
    }

    @Test
    @DisplayName("Enroll + available/enrolled lists корректны")
    void enrollFlow() {
        assertThat(studentService.getEnrolledCourses(studentId)).isEmpty();
        studentService.enrollStudentInCourse(studentId, courseA);
        var enrolled = studentService.getEnrolledCourses(studentId);
        assertThat(enrolled).hasSize(1).first().extracting(CourseBriefDto::name).isEqualTo("Course A");
        var available = studentService.getAvailableCourses(studentId);
        assertThat(available).extracting(CourseBriefDto::name).contains("Course B");
    }

    @Test
    @DisplayName("Unenroll удаляет курс из списка")
    void unenrollFlow() {
        studentService.enrollStudentInCourse(studentId, courseB);
        assertThat(studentService.getEnrolledCourses(studentId)).extracting(CourseBriefDto::name).contains("Course B");
        studentService.unenrollStudentFromCourse(studentId, courseB);
        assertThat(studentService.getEnrolledCourses(studentId)).extracting(CourseBriefDto::name).doesNotContain("Course B");
    }
}
