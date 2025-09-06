# Security Role System Documentation

## 📋 Overview

This document describes the comprehensive role-based access control (RBAC) system implemented for the Education Control System (ECS).

## 🎯 Roles Hierarchy

### Administrative Roles

1. **SUPER_ADMIN** - System superuser with full access
2. **ADMIN** - Educational administrator
3. **MANAGER** - Educational manager

### Educational Roles  

1. **TEACHER** - Course instructor
2. **STUDENT** - Course participant

### Specialized Roles

1. **ANALYST** - Data analyst (read-only analytics)
2. **MODERATOR** - Content moderator

### Basic Role

1. **GUEST** - Public access (limited)

## 🔑 Key Permissions by Role

### SUPER_ADMIN

- ✅ Full system management
- ✅ User management (all roles)
- ✅ System settings and backup
- ✅ Audit log access
- ✅ All course operations
- ✅ All analytics

### ADMIN  

- ✅ Course management (all)
- ✅ Student/Teacher management
- ✅ Analytics and reports
- ✅ Schedule management
- ❌ System settings

### MANAGER

- ✅ Course creation/editing
- ✅ Teacher assignment
- ✅ Basic analytics
- ✅ Student communication
- ❌ User account management

### TEACHER

- ✅ Own course management
- ✅ Own student management
- ✅ Task/grade management
- ✅ Own course analytics
- ❌ Other teachers' courses

### STUDENT

- ✅ Course viewing/enrollment
- ✅ Task submission
- ✅ Own grade viewing
- ✅ Own progress tracking
- ❌ Course management

### ANALYST

- ✅ All analytics viewing
- ✅ Report generation
- ✅ Data export
- ❌ Data modification

### MODERATOR

- ✅ Content moderation
- ✅ Student applications
- ✅ Technical support
- ❌ Educational content

### GUEST

- ✅ Public course catalog
- ✅ Registration request
- ❌ Authentication required features

## 🛠️ Implementation Files

### Core Files

- `Role.java` - Main role enumeration with permissions
- `Permission.java` - Granular permission definitions  
- `SecurityUtils.java` - Utility methods for role/permission checking
- `SecurityAnnotations.java` - Custom annotations for easier access control

### Usage Examples

#### Controller Methods

```java
@AdminOnly
@PostMapping("/courses/{id}/delete")
public String deleteCourse(@PathVariable int id) {
    // Only SUPER_ADMIN and ADMIN can access
}

@CourseManagement  
@GetMapping("/courses/new")
public String showCreateForm() {
    // SUPER_ADMIN, ADMIN, MANAGER, TEACHER can access
}

@StudentOnly
@GetMapping("/my-courses")  
public String showMyCourses() {
    // Only STUDENT role can access
}
```

#### Programmatic Checks

```java
// Check specific role
if (SecurityUtils.hasRole(userDetails, Role.TEACHER)) {
    // Teacher-specific logic
}

// Check permission
if (SecurityUtils.hasPermission(userDetails, Permission.COURSE_CREATE)) {
    // User can create courses
}

// Check multiple roles
if (SecurityUtils.hasAnyRole(userDetails, Role.ADMIN, Role.MANAGER)) {
    // Administrative logic
}
```

#### Template Usage (with Thymeleaf Security)

```html
<!-- Show only for administrators -->
<div sec:authorize="hasAnyRole('SUPER_ADMIN', 'ADMIN')">
    <a href="/admin/settings">Admin Settings</a>
</div>

<!-- Show for course managers -->
<div sec:authorize="hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')">
    <a href="/courses/new">Create Course</a>
</div>

<!-- Show current user role -->
<span>Logged in as: <span sec:authentication="authorities"></span></span>
```

## 🚀 Next Steps

To complete the implementation:

1. **Add Thymeleaf Security dependency**
2. **Update SecurityConfig with role-based access**  
3. **Apply annotations to controller methods**
4. **Create User entity with roles**
5. **Update navigation templates**
6. **Add role management UI**

## 📖 Benefits

- **Granular Control** - Fine-grained permissions for specific operations
- **Scalable** - Easy to add new roles and permissions
- **Maintainable** - Clear separation of concerns
- **Secure** - Principle of least privilege
- **Flexible** - Multiple ways to check access (annotations, programmatic, templates)
