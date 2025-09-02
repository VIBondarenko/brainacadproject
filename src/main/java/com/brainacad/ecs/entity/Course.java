package com.brainacad.ecs.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Course entity representing educational courses
 * JPA entity with proper relationships to Student and Trainer
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Size(max = 100, message = "Course name cannot exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Begin date is required")
    @Column(name = "begin_date", nullable = false)
    private LocalDate beginDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Size(max = 50, message = "Days cannot exceed 50 characters")
    @Column(name = "days", length = 50)
    private String days;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private final Set<Student> students = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final Set<Task> tasks = new HashSet<>();

    // Constructors
    protected Course() {
        // JPA requires default constructor
    }

    public Course(String name, String description) {
        this.name = name;
        this.description = description;
        this.beginDate = LocalDate.now();
        this.endDate = LocalDate.now().plusMonths(3);
    }

    public Course(String name, String description, LocalDate beginDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public Course(String name, String description, LocalDate beginDate, LocalDate endDate, String days) {
        this.name = name;
        this.description = description;
        this.beginDate = beginDate;
        this.endDate = endDate;
        this.days = days;
    }

    // JPA Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(LocalDate beginDate) {
        this.beginDate = beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Set<Student> getStudents() {
        return new HashSet<>(students);
    }

    public Set<Task> getTasks() {
        return new HashSet<>(tasks);
    }

    // Helper methods for managing relationships
    public void addStudent(Student student) {
        if (student != null) {
            students.add(student);
            student.getCourses().add(this);
        }
    }

    public void removeStudent(Student student) {
        if (student != null) {
            students.remove(student);
            student.getCourses().remove(this);
        }
    }

    public void addTask(Task task) {
        if (task != null) {
            tasks.add(task);
            task.setCourse(this);
        }
    }

    public void removeTask(Task task) {
        if (task != null) {
            tasks.remove(task);
            task.setCourse(null);
        }
    }

    // Business methods
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(beginDate) && !now.isAfter(endDate);
    }

    public boolean isUpcoming() {
        return LocalDate.now().isBefore(beginDate);
    }

    public boolean isCompleted() {
        return LocalDate.now().isAfter(endDate);
    }

    public int getStudentCount() {
        return students.size();
    }

    public int getTaskCount() {
        return tasks.size();
    }

    // toString, equals, hashCode
    @Override
    public String toString() {
        return String.format("Course{id=%d, name='%s', beginDate=%s, endDate=%s, studentCount=%d}",
                id, name, beginDate, endDate, getStudentCount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(id, course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
