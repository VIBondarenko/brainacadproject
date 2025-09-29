package io.github.vibondarenko.clavionx.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.github.vibondarenko.clavionx.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Manager entity representing a user with manager role
 * Responsible for administrative and management tasks
 */
@Entity
@Table(name = "managers")
public class Manager extends User {

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(name = "team_size")
    private Integer teamSize;

    @Column(name = "budget_limit", precision = 12, scale = 2)
    private BigDecimal budgetLimit;

    // Constructors
    protected Manager() {
        super();
    }

    public Manager(String name, String lastName, String username, String password, String email) {
        super(name, lastName, username, password, email, Role.MANAGER);
        this.hireDate = LocalDate.now();
    }

    public Manager(String name, String lastName, Integer age, String username, String password, String email) {
        super(name, lastName, age, username, password, email, Role.MANAGER);
        this.hireDate = LocalDate.now();
    }

    public Manager(String name, String lastName, String username, String password, String email,
                    String department, BigDecimal salary, Integer teamSize) {
        super(name, lastName, username, password, email, Role.MANAGER);
        this.hireDate = LocalDate.now();
        this.department = department;
        this.salary = salary;
        this.teamSize = teamSize;
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Integer getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(Integer teamSize) {
        this.teamSize = teamSize;
    }

    public BigDecimal getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(BigDecimal budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\tDepartment: " + department + "\n" +
                "\tHire Date: " + hireDate + "\n" +
                "\tSalary: " + salary + "\n" +
                "\tTeam Size: " + teamSize + "\n" +
                "\tBudget Limit: " + budgetLimit + "\n";
    }

    /**
     * Checks equality based on Manager fields and superclass.
     * @param o object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Manager)) return false;
        if (!super.equals(o)) return false;
        Manager manager = (Manager) o;
        return java.util.Objects.equals(department, manager.department)
                && java.util.Objects.equals(hireDate, manager.hireDate)
                && java.util.Objects.equals(salary, manager.salary)
                && java.util.Objects.equals(teamSize, manager.teamSize)
                && java.util.Objects.equals(budgetLimit, manager.budgetLimit);
    }

    /**
     * Returns hash code based on Manager fields and superclass.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                super.hashCode(),
                department,
                hireDate,
                salary,
                teamSize,
                budgetLimit
        );
    }
}