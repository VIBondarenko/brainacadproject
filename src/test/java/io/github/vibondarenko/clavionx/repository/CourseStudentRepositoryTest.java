package io.github.vibondarenko.clavionx.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import io.github.vibondarenko.clavionx.entity.Course;
import io.github.vibondarenko.clavionx.entity.Student;
import io.github.vibondarenko.clavionx.security.Role;

@DataJpaTest
@ActiveProfiles("test")
class CourseStudentRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private StudentRepository studentRepository;

    private Student newStudent(String fn, String ln, String email) {
        Student s = new Student(fn, ln, fn.toLowerCase()+"."+ln.toLowerCase(), "pass", email);
        s.setStudentNumber("STU-"+System.nanoTime());
        s.setGraduationDate(LocalDate.now().plusYears(1));
        s.setRole(Role.STUDENT);
        s.setEnabled(true);
        return s;
    }

    private Course newCourse(String name) {
        Course c = new Course(name, name+" desc", LocalDate.now(), LocalDate.now().plusMonths(2));
        return c;
    }

    @Test
    @DisplayName("findByCourseId возвращает студентов курса")
    void findByCourseId() {
        Course course = courseRepository.save(newCourse("Math"));
        Student s1 = studentRepository.save(newStudent("John","Doe","j@ex.com"));
        Student s2 = studentRepository.save(newStudent("Alice","Roe","a@ex.com"));
        s1.addCourse(course); course.addStudent(s1);
        s2.addCourse(course); course.addStudent(s2);
        studentRepository.saveAll(List.of(s1,s2));
        courseRepository.save(course);

        List<Student> list = studentRepository.findByCourseId(course.getId());
        assertThat(list).hasSize(2).extracting(Student::getUsername).contains(s1.getUsername(), s2.getUsername());
    }

    @Test
    @DisplayName("findCoursesNotEnrolledByStudent возвращает курсы без студента")
    void findCoursesNotEnrolledByStudent() {
        Student s = studentRepository.save(newStudent("Bob","Lee","b@ex.com"));
        Course c1 = courseRepository.save(newCourse("Physics"));
    courseRepository.save(newCourse("Chemistry"));
        c1.addStudent(s); s.addCourse(c1); courseRepository.save(c1); studentRepository.save(s);

        List<Course> notEnrolled = courseRepository.findCoursesNotEnrolledByStudent(s.getId());
        assertThat(notEnrolled).hasSize(1).first().extracting(Course::getName).isEqualTo("Chemistry");
    }
}
