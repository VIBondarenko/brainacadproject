package com.brainacad.ecs.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.brainacad.ecs.security.Role;

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
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    // Constructors
    protected Student() {
        super();
    }

    public Student(String name, String lastName, String username, String password) {
        super(name, lastName, username, password, Role.STUDENT);
        this.enrollmentDate = LocalDate.now();
    }

    public Student(String name, String lastName, Integer age, String username, String password) {
        super(name, lastName, age, username, password, Role.STUDENT);
        this.enrollmentDate = LocalDate.now();
    }

    public Student(String name, String lastName, String username, String password, 
                   String studentNumber, Integer graduationYear) {
        super(name, lastName, username, password, Role.STUDENT);
        this.enrollmentDate = LocalDate.now();
        this.studentNumber = studentNumber;
        this.graduationYear = graduationYear;
    }

    // Business methods
    public void addCourse(@NotNull Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null");
        }
        if (!courses.contains(course)) {
            courses.add(course);
            // TODO: Implement course.addStudent(this) when Course is refactored to JPA
        }
    }

    public void removeCourse(@NotNull Course course) {
        if (courses.remove(course)) {
            // TODO: Implement course.removeStudent(this) when Course is refactored to JPA
        }
    }

    public boolean addTask(@NotNull Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (!tasks.contains(task)) {
            tasks.add(task);
            // TODO: Implement task.setStudent(this) when Task is refactored to JPA
            return true;
        }
        return false;
    }

    public boolean removeTask(@NotNull Task task) {
        // TODO: Implement task.setStudent(null) when Task is refactored to JPA
        return tasks.remove(task);
    }

    public void addTasksFromCourse(Long courseId) {
        courses.stream()
                .filter(course -> Objects.equals(course.getId(), courseId))
                .findFirst()
                .ifPresent(course -> {
                    // TODO: Implement course.getTasks().forEach(this::addTask) when Course is refactored to JPA
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
}
