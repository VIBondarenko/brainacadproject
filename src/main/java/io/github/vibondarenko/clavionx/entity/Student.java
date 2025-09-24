package io.github.vibondarenko.clavionx.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.vibondarenko.clavionx.security.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Student entity representing a user with student role
 * Inherits authentication from User and adds student-specific fields
 */
@Entity
@Table(name = "students")
@DiscriminatorValue("STUDENT")
public class Student extends User {

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "student_number", unique = true, length = 20)
    private String studentNumber;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_courses",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @SuppressWarnings("java:S1948")
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @SuppressWarnings("java:S1948")
    private List<Task> tasks = new ArrayList<>();

    // Constructors
    protected Student() {
        super();
    }

    public Student(String name, String lastName, String username, String password, String email) {
        super(name, lastName, username, password, email, Role.STUDENT);
        this.enrollmentDate = LocalDate.now();
    }

    public Student(String name, String lastName, Integer age, String username, String password, String email) {
        super(name, lastName, age, username, password, email, Role.STUDENT);
        this.enrollmentDate = LocalDate.now();
    }

    public Student(String name, String lastName, String username, String password, String email,
                    String studentNumber, Integer graduationYear) {
        super(name, lastName, username, password, email, Role.STUDENT);
        this.enrollmentDate = LocalDate.now();
        this.studentNumber = studentNumber;
        this.graduationYear = graduationYear;
    }

    // Business methods
    public void addCourse(@NotNull Course course) {
        if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    public void removeCourse(@NotNull Course course) {
        if (courses.contains(course)) {
            courses.remove(course);
        }
    }

    public boolean addTask(@NotNull Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
            return true;
        }
        return false;
    }

    public boolean removeTask(@NotNull Task task) {
        return tasks.remove(task);
    }

    public void addTasksFromCourse(Long courseId) {
        courses.stream()
                .filter(course -> Objects.equals(course.getId(), courseId))
                .findFirst()
                .ifPresent(course -> {
                });
    }

    public void removeTasksFromCourse(Long courseId) {
        tasks.removeIf(task -> task.getCourse() != null && 
                        Objects.equals(task.getCourse().getId(), courseId));
    }

    public boolean isEnrolledInCourse(Long courseId) {
        return courses.stream()
                .anyMatch(course -> Objects.equals(course.getId(), courseId));
    }

    public List<Task> getTasksForCourse(Long courseId) {
        return tasks.stream()
                .filter(task -> task.getCourse() != null && 
                        Objects.equals(task.getCourse().getId(), courseId))
                .toList();
    }

    // Getters and Setters
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Integer getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(Integer graduationYear) {
        this.graduationYear = graduationYear;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    @Override
    public String toString() {
        return super.toString() +
                "\tStudent Number: " + studentNumber + "\n" +
                "\tEnrollment Date: " + enrollmentDate + "\n" +
                "\tGraduation Year: " + graduationYear + "\n" +
                "\tCourses Count: " + courses.size() + "\n" +
                "\tTasks Count: " + tasks.size() + "\n";
    }

    /**
     * Compares this Student to another object for equality.
     * @param o the object to compare
     * @return true if the objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(studentNumber, student.studentNumber) &&
                Objects.equals(enrollmentDate, student.enrollmentDate) &&
                Objects.equals(graduationYear, student.graduationYear);
    }

    /**
     * Returns a hash code value for the Student.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), studentNumber, enrollmentDate, graduationYear);
    }
}