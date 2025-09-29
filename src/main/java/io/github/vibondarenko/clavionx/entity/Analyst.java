package io.github.vibondarenko.clavionx.entity;

import java.time.LocalDate;

import io.github.vibondarenko.clavionx.security.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Analyst entity representing a user with analyst role
 * Specializes in analytics and reporting tasks
 * Extends the User class
 * Includes fields for analytics level, reporting access, certification, and hire date
 * Provides constructors, getters, setters, and overrides for toString, equals, and hashCode methods
 * Mapped to "analysts" table in the database
 * Uses "ANALYST" as the discriminator value for inheritance
 */
@Entity
@Table(name = "analysts")
public class Analyst extends User {

    @Column(name = "analytics_level", length = 50)
    private String analyticsLevel;

    @Column(name = "reporting_access")
    private boolean reportingAccess = true;

    @Column(name = "certification", length = 100)
    private String certification;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    // Constructors
    protected Analyst() {
        super();
    }

    public Analyst(String name, String lastName, String username, String password, String email) {
        super(name, lastName, username, password, email, Role.ANALYST);
        this.hireDate = LocalDate.now();
    }

    public Analyst(String name, String lastName, Integer age, String username, String password, String email) {
        super(name, lastName, age, username, password, email, Role.ANALYST);
        this.hireDate = LocalDate.now();
    }

    public Analyst(String name, String lastName, String username, String password, String email,
                    String analyticsLevel, String certification) {
        super(name, lastName, username, password, email, Role.ANALYST);
        this.hireDate = LocalDate.now();
        this.analyticsLevel = analyticsLevel;
        this.certification = certification;
    }

    // Getters and Setters
    public String getAnalyticsLevel() {
        return analyticsLevel;
    }

    public void setAnalyticsLevel(String analyticsLevel) {
        this.analyticsLevel = analyticsLevel;
    }

    public boolean isReportingAccess() {
        return reportingAccess;
    }

    public void setReportingAccess(boolean reportingAccess) {
        this.reportingAccess = reportingAccess;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\tAnalytics Level: " + analyticsLevel + "\n" +
                "\tReporting Access: " + reportingAccess + "\n" +
                "\tCertification: " + certification + "\n" +
                "\tHire Date: " + hireDate + "\n";
    }

    /**
     * Compares this Analyst to another object for equality.
     * @param o the object to compare
     * @return true if objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Analyst)) return false;
        if (!super.equals(o)) return false;
        Analyst analyst = (Analyst) o;
        return reportingAccess == analyst.reportingAccess &&
                java.util.Objects.equals(analyticsLevel, analyst.analyticsLevel) &&
                java.util.Objects.equals(certification, analyst.certification) &&
                java.util.Objects.equals(hireDate, analyst.hireDate);
    }

    /**
     * Returns a hash code value for the object.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(super.hashCode(), analyticsLevel, reportingAccess, certification, hireDate);
    }
}