package io.github.vibondarenko.clavionx.entity;

import java.math.BigDecimal;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Trainer entity representing a user with trainer/teacher role
 * Inherits authentication from User and adds trainer-specific fields
 */
@Entity
@Table(name = "trainers")
@DiscriminatorValue("TRAINER")
public class Trainer extends User {

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;
    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @SuppressWarnings("java:S1948")
    private List<Course> courses = new ArrayList<>();
    
    // Constructors
    protected Trainer() {
        super();
    }

    public Trainer(String name, String lastName, String username, String password, String email) {
        super(name, lastName, username, password, email, Role.TEACHER);
        this.hireDate = LocalDate.now();
    }

    public Trainer(String name, String lastName, Integer age, String username, String password, String email) {
        super(name, lastName, age, username, password, email, Role.TEACHER);
        this.hireDate = LocalDate.now();
    }

    public Trainer(String name, String lastName, String username, String password, String email,
                    String department, String specialization, BigDecimal salary) {
        super(name, lastName, username, password, email, Role.TEACHER);
        this.hireDate = LocalDate.now();
        this.department = department;
        this.specialization = specialization;
        this.salary = salary;
    }

    // Business methods
    public void addCourse(@NotNull Course course) {
    if (!courses.contains(course)) {
            courses.add(course);
        }
    }

    public boolean removeCourse(@NotNull Course course) {
        return courses.remove(course);
    }

    public void removeFromAllCourses() {
        for (Course course : new ArrayList<>(courses)) {
            removeCourse(course);
        }
    }

    public boolean isTeachingCourse(Long courseId) {
        return courses.stream()
                .anyMatch(course -> Objects.equals(course.getId(), courseId));
    }

    // Getters and Setters
    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
    }

    @Override
    public String toString() {
        return super.toString() +
                "\tDepartment: " + department + "\n" +
                "\tSpecialization: " + specialization + "\n" +
                "\tHire Date: " + hireDate + "\n" +
                "\tSalary: " + salary + "\n" +
                "\tCourses Count: " + courses.size() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer)) return false;
        if (!super.equals(o)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(hireDate, trainer.hireDate) &&
                Objects.equals(department, trainer.department) &&
                Objects.equals(specialization, trainer.specialization) &&
                Objects.equals(salary, trainer.salary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hireDate, department, specialization, salary);
    }
}