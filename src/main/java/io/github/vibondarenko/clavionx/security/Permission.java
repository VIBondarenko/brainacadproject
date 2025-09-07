package io.github.vibondarenko.clavionx.security;

/**
 * Granular permissions for the Education Control System
 * These permissions provide fine-grained access control
 */
public enum Permission {
    
    // ========== SYSTEM MANAGEMENT ==========
    /**
     * Full system administration access
     */
    SYSTEM_MANAGE("System Management", "Manage system settings, configuration, and infrastructure"),
    
    /**
     * Backup and restore operations
     */
    BACKUP_MANAGE("Backup Management", "Create and restore system backups"),
    
    /**
     * System settings and configuration
     */
    SETTINGS_MANAGE("Settings Management", "Modify system settings and configuration"),
    
    /**
     * Audit log access
     */
    AUDIT_VIEW("Audit Viewing", "View system audit logs and security events"),
    
    // ========== USER MANAGEMENT ==========
    /**
     * Full user management across all roles
     */
    USER_MANAGE_ALL("All User Management", "Create, edit, delete any user account"),
    
    /**
     * Student management permissions
     */
    STUDENT_MANAGE_ALL("All Student Management", "Manage all student accounts and enrollments"),
    
    /**
     * Teacher management permissions
     */
    TEACHER_MANAGE_ALL("All Teacher Management", "Manage all teacher accounts and assignments"),
    
    /**
     * Assign teachers to courses
     */
    TEACHER_ASSIGN("Teacher Assignment", "Assign teachers to courses"),
    
    /**
     * View all students
     */
    STUDENT_VIEW_ALL("View All Students", "View information about all students"),
    
    /**
     * Manage students in own courses only
     */
    STUDENT_MANAGE_OWN("Own Student Management", "Manage students enrolled in own courses"),
    
    /**
     * Handle student applications and registrations
     */
    STUDENT_APPLICATIONS_MANAGE("Student Applications", "Process student applications and registrations"),
    
    // ========== COURSE MANAGEMENT ==========
    /**
     * Full course management permissions
     */
    COURSE_MANAGE_ALL("All Course Management", "Create, edit, delete any course"),
    
    /**
     * Create new courses
     */
    COURSE_CREATE("Course Creation", "Create new courses"),
    
    /**
     * Edit any course
     */
    COURSE_EDIT_ALL("Edit All Courses", "Edit any course in the system"),
    
    /**
     * Create own courses (for teachers)
     */
    COURSE_CREATE_OWN("Own Course Creation", "Create courses that you will teach"),
    
    /**
     * Edit own courses only
     */
    COURSE_EDIT_OWN("Own Course Editing", "Edit only courses that you teach"),
    
    /**
     * View course information
     */
    COURSE_VIEW("Course Viewing", "View course details and information"),
    
    /**
     * View only public course information
     */
    COURSE_VIEW_PUBLIC("Public Course Viewing", "View public course catalog"),
    
    /**
     * Enroll in courses
     */
    COURSE_ENROLL("Course Enrollment", "Enroll in available courses"),
    
    /**
     * Manage course schedules
     */
    SCHEDULE_MANAGE("Schedule Management", "Create and modify course schedules"),
    
    // ========== TASK AND GRADING ==========
    /**
     * Manage tasks in own courses
     */
    TASK_MANAGE_OWN("Own Task Management", "Create and manage tasks in own courses"),
    
    /**
     * Submit tasks as a student
     */
    TASK_SUBMIT("Task Submission", "Submit assignments and tasks"),
    
    /**
     * Grade students in own courses
     */
    GRADE_MANAGE_OWN("Own Grade Management", "Grade students in own courses"),
    
    /**
     * View own grades
     */
    GRADE_VIEW_OWN("Own Grade Viewing", "View own grades and feedback"),
    
    // ========== ANALYTICS AND REPORTS ==========
    /**
     * View all system analytics
     */
    ANALYTICS_VIEW_ALL("All Analytics", "View comprehensive system analytics"),
    
    /**
     * View limited analytics
     */
    ANALYTICS_VIEW_LIMITED("Limited Analytics", "View basic system statistics"),
    
    /**
     * View own course/student analytics
     */
    ANALYTICS_VIEW_OWN("Own Analytics", "View analytics for own courses or progress"),
    
    /**
     * Generate system reports
     */
    REPORTS_GENERATE("Report Generation", "Create and generate system reports"),
    
    /**
     * View existing reports
     */
    REPORTS_VIEW("Report Viewing", "View existing system reports"),
    
    /**
     * View reports for own courses
     */
    REPORTS_VIEW_OWN("Own Report Viewing", "View reports for own courses or progress"),
    
    /**
     * Export data from the system
     */
    DATA_EXPORT("Data Export", "Export system data to external formats"),
    
    /**
     * View system statistics
     */
    STATISTICS_VIEW("Statistics Viewing", "View system usage statistics"),
    
    // ========== CONTENT MODERATION ==========
    /**
     * Moderate user-generated content
     */
    CONTENT_MODERATE("Content Moderation", "Review and moderate user-generated content"),
    
    /**
     * Moderate communications
     */
    COMMUNICATION_MODERATE("Communication Moderation", "Moderate user communications and messages"),
    
    /**
     * Send system communications
     */
    COMMUNICATION_SEND("Communication Sending", "Send announcements and messages"),
    
    // ========== PERSONAL PERMISSIONS ==========
    /**
     * Edit own profile
     */
    PROFILE_EDIT_OWN("Own Profile Editing", "Edit own user profile and settings"),
    
    /**
     * View own progress
     */
    PROGRESS_VIEW_OWN("Own Progress Viewing", "View own learning progress and achievements"),
    
    /**
     * Provide technical support
     */
    SUPPORT_PROVIDE("Support Provision", "Provide technical support to users"),
    
    /**
     * Request system registration
     */
    REGISTRATION_REQUEST("Registration Request", "Request account registration");
    
    private final String displayName;
    private final String description;
    
    Permission(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Get Spring Security authority string for this permission
     * @return Authority string in format "PERMISSION_NAME"
     */
    public String getAuthority() {
        return "PERMISSION_" + this.name();
    }
    
    /**
     * Check if this permission is administrative
     * @return true if this is an administrative permission
     */
    public boolean isAdministrative() {
        return this.name().contains("MANAGE") && 
                (this.name().contains("ALL") || this.name().contains("SYSTEM"));
    }
    
    /**
     * Check if this permission is educational (related to courses/learning)
     * @return true if this is an educational permission
     */
    public boolean isEducational() {
        return this.name().contains("COURSE") || 
                this.name().contains("TASK") || 
                this.name().contains("GRADE") ||
                this.name().contains("STUDENT");
    }
}



