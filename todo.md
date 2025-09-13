# 📝 Todo List 📝

## 🔥 Next Priority Tasks 🔥

### 🛡️ Enhance security features

- - [x] **(11.09.2025 00:00:00)** Add phone number to the Person class
- - [x] **(11.09.2025 00:00:00)** Move email to Person class with validation and flexible login (username or email)
- - [x] **(11.09.2025 00:00:00)** Add phone number field to the user create/edit/view page
- - [x] **(11.09.2025 00:00:00)** Add phone number field to the profile edit page
- - [x] **(11.09.2025 00:00:00)** Two-factor authentication (2FA). Save 2FA. 2FA via email or phone number
- - [x] **(11.09.2025 00:00:00)** Backend Infrastructure (Entities, Services, Repositories)
- - [x] **(11.09.2025 00:00:00)** Spring Security Integration (Providers, Handlers, Tokens)  
- - [x] **(11.09.2025 00:00:00)** 2FA Verification Controller and Templates
- - [x] **(11.09.2025 00:00:00)** Frontend Settings Page for 2FA Management
- - [x] **(12.09.2025 22:15:00)** Fixed preHandle method in EnhancedSessionTrackingInterceptor to not always return same value
- - [x] **(12.09.2025 22:25:00)** Fixed EnhancedSessionTrackingInterceptorTest afterCompletion test logic
- - [ ] Password policy enforcement
- - [ ] Account lockout protection
- - [ ] Login notifications: Get notified of new logins

### 🔔 Add notification system

- - [ ] Email notifications for important events
- - [ ] In-app notification center
- - [ ] Customizable notification preferences

---

## ✅ Completed Tasks ✅

### ✅ Code Quality & Configuration Improvements

- - [x] **(11.09.2025 00:00:00)** Added SonarQube configuration to pom.xml with proper report paths for JUnit, JaCoCo coverage
- - [x] **(11.09.2025 00:00:00)** Refactored TwoFactorAuthenticationSuccessHandler using modern pattern matching
- - [x] **(11.09.2025 00:00:00)** Added equals() and hashCode() methods to TwoFactorAuthenticationToken class  
- - [x] **(11.09.2025 00:00:00)** Improved Person.equals() method for better field comparison order
- - [x] **(11.09.2025 00:00:00)** Created custom SessionTerminationException instead of generic RuntimeException
- - [x] **(11.09.2025 00:00:00)** Fixed code formatting and removed unnecessary imports
- - [x] **(11.09.2025 00:00:00)** Updated unit tests with better assertions and corrected test logic
- - [x] **(11.09.2025 21:10:00)** Migrated logging from java.util.logging (JUL) to SLF4J across core classes (services, config, main app)
- - [x] **(13.09.2025 00:00:00)** Activation: redirect to login on successful account activation (fix static return warning)

### ✅ Database Initialization

- - [x] **(11.09.2025 00:00:00)** Created DataInitializer component for default admin user creation
- - [x] **(11.09.2025 00:00:00)** Removed obsolete EmailMigrationService (legacy migration for users without emails)

### ✅ Maven Configuration Updates

- - [x] **(11.09.2025 00:00:00)** Updated Maven compiler configuration to use release option instead of source/target for strict compatibility checks

### ✅ **(11.09.2025 00:00:00)** Test Infrastructure Setup

- - [x] **(11.09.2025 00:00:00)** Fixed failing tests by adding H2 database dependency for test scope
- - [x] **(11.09.2025 00:00:00)** Created ActivityApiControllerSimpleTest with proper H2 in-memory database configuration
- - [x] **(11.09.2025 00:00:00)** Resolved database driver conflicts between PostgreSQL (production) and H2 (testing)
- - [x] **(11.09.2025 00:00:00)** All tests now execute successfully

### � Spring Security cleanup

- - [x] **(11.09.2025 00:00:00)** Removed deprecated DaoAuthenticationProvider bean
- - [x] **(11.09.2025 00:00:00)** Configured authentication via HttpSecurity with UserDetailsService and existing PasswordEncoder
- - [x] **(11.09.2025 00:00:00)** Kept TwoFactorAuthenticationProvider registration intact

### 🧭 Centralize roles and paths

- - [x] **(11.09.2025 00:00:00)** Added Roles and Paths constants to eliminate duplicated literals and satisfy Sonar rule S1192
- - [x] **(11.09.2025 00:00:00)** Reworked SecurityConfig to use centralized constants and grouped role arrays
- - [x] **(11.09.2025 00:00:00)** Introduced composed annotations (AdminOnly, AnalyticsAccess, DashboardAccess, etc.) for @PreAuthorize
- - [x] **(11.09.2025 00:00:00)** Updated ActivityController to use composed annotations
- - [x] **(11.09.2025 00:00:00)** Switched composed annotations to use enum Role in SpEL (type-safe, no string literals)

### �📄 Create Help Page

- - [x] **(11.09.2025 00:00:00)** Added HelpController with @GetMapping for /help endpoint
- - [x] **(11.09.2025 00:00:00)** Created help.html template following About page pattern
- - [x] **(11.09.2025 00:00:00)** Updated breadcrumbs and navigation structure
- - [x] **(11.09.2025 00:00:00)** Added comprehensive help content with FAQ accordion
- - [x] **(11.09.2025 00:00:00)** Tested and verified functionality
- - [x] **(11.09.2025 00:00:00)** **Navigation Enhancement**: Replaced simple "About" link with dropdown menu containing "About ClavionX" and "Help" options

### 📄 Creating common pages

- - [x] **(11.09.2025 00:00:00)** About
- - [x] **(11.09.2025 00:00:00)** Help

### ℹ️ Application Information

- - [x] **(11.09.2025 00:00:00)** Build info integration with templates - completed
- - [x] **(11.09.2025 00:00:00)** GlobalModelAttributes @ControllerAdvice creation
- - [x] **(11.09.2025 00:00:00)** Template updates (base.html, login.html) - completed
- - [x] **(11.09.2025 00:00:00)** Two commits made with build info changes - completed
- - [x] **(11.09.2025 00:00:00)** Database migration analysis for 2FA - completed (migrations V006/V10 not needed, schema already exists)

### 🖼️ Avatar upload functionality

- - [x] **(11.09.2025 00:00:00)** Modal window opens correctly on main profile page
- - [x] **(11.09.2025 00:00:00)** JavaScript initialization works perfectly
- - [x] **(11.09.2025 00:00:00)** File upload, validation, and preview functionality operational
- - [x] **(11.09.2025 00:00:00)** Backend processing, database storage, and file saving working
- - [x] **(11.09.2025 00:00:00)** Success message and page refresh after upload implemented
- - [x] **(11.09.2025 00:00:00)** Both upload paths working: main profile page and /profile/edit
- - [x] **(11.09.2025 00:00:00)** UUID-based file naming and security measures in place

### 🛡️ Enhanced Session Management

- - [x] **(11.09.2025 00:00:00)** Comprehensive session filtering (Active/Inactive/All)
- - [x] **(11.09.2025 00:00:00)** Enhanced UI with status tabs and visual indicators
- - [x] **(11.09.2025 00:00:00)** Session cleanup logic with scheduled tasks
- - [x] **(11.09.2025 00:00:00)** Session monitoring and analytics service
- - [x] **(11.09.2025 00:00:00)** Enhanced session tracking interceptor
- - [x] **(11.09.2025 00:00:00)** Configurable session parameters for all environments
- - [x] **(11.09.2025 00:00:00)** Fixed inactive session display issue (active=false sessions now visible)

### 🔐 2FA Authentication Flow Fixed

- - [x] **(11.09.2025 00:00:00)** Fixed LoginSuccessHandler to properly integrate 2FA with Remember Me logic
- - [x] **(11.09.2025 00:00:00)** Modified SecurityConfig to allow /auth/2fa access during authentication process
- - [x] **(11.09.2025 00:00:00)** Resolved HTTP 403 error when accessing 2FA verification page
- - [x] **(11.09.2025 00:00:00)** Implemented proper RememberMeAuthenticationToken detection
- - [x] **(11.09.2025 00:00:00)** 2FA now correctly required for fresh logins, skipped for Remember Me sessions

### 🖼️ Enhancement avatar functionality

- - [x] **(11.09.2025 00:00:00)** Add image preview before upload
- - [x] **(11.09.2025 00:00:00)** Add loading indicators
- - [x] **(11.09.2025 00:00:00)** Implement drag-and-drop upload
- - [x] **(11.09.2025 00:00:00)** Add loading indicators
- - [x] **(11.09.2025 00:00:00)** Support multiple image formats (PNG, GIF)
- - [x] **(11.09.2025 00:00:00)** Add image compression/resize capabilities

### 🔑 Implement password recovery

- - [x] **(11.09.2025 00:00:00)** Add password reset request form
- - [x] **(11.09.2025 00:00:00)** Email with reset link
- - [x] **(11.09.2025 00:00:00)** Secure token validation and password change
- - [x] **(11.09.2025 00:00:00)** Add a "Confirm new password" field to the password reset page for confirmation
- - [x] **(11.09.2025 00:00:00)** Add password complexity validation, same as on the user password change page
- - [x] **(11.09.2025 00:00:00)** JavaScript validation with real-time feedback and form control
- - [x] **(11.09.2025 00:00:00)** Proper Thymeleaf layout fragment integration
