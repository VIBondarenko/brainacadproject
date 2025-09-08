# 📝 Todo List 📝

## 🔥 Next Priority Tasks 🔥

### 📄 Creating common pages

- - [x] About
- - [x] Help
  
### 🛡️ Enhance security features

- - [x] Add phone number to the Person class
- - [x] Move email to Person class with validation and flexible login (username or email)
- - [x] Add phone number field to the user create/edit/view page
- - [x] Add phone number field to the profile edit page
- - [x] Two-factor authentication (2FA). Save 2FA. 2FA via email or phone number
- - [x] Backend Infrastructure (Entities, Services, Repositories)
- - [x] Spring Security Integration (Providers, Handlers, Tokens)  
- - [x] 2FA Verification Controller and Templates
- - [x] Frontend Settings Page for 2FA Management
- - [ ] Password policy enforcement
- - [ ] Account lockout protection
- - [ ] Login notifications: Get notified of new logins

### 🕒 Remember session

- - [ ] Set remember-me token validity to 1 day for login, session timeout to 1 minute for non-remembered users (done in config). Persistent token repository can be added for extra security.
- - [ ] Task List for Improvements

### 📊 Implement real user activity history

- - [ ] Enhanced activity tracking with more detail
- - [ ] Activity filtering and search
- - [ ] Export functionality for audit reports

### 🔔 Add notification system

- - [ ] Email notifications for important events
- - [ ] In-app notification center
- - [ ] Customizable notification preferences

### 📚 Improve course management

- - [ ] Course materials upload
- - [ ] Assignment submission system
- - [ ] Grade tracking

### 📊 Add reporting dashboard

- - [ ] Student progress reports
- - [ ] Course completion statistics
- - [ ] Custom report generation

## ✅ Completed Tasks ✅

### ℹ️ Application Information

- - [x] Build info integration with templates - completed
- - [x] GlobalModelAttributes @ControllerAdvice creation
- - [x] Template updates (base.html, login.html) - completed
- - [x] Two commits made with build info changes - completed
- - [x] Database migration analysis for 2FA - completed (migrations V006/V10 not needed, schema already exists)

### 🖼️ Avatar upload functionality

- - [x] Modal window opens correctly on main profile page
- - [x] JavaScript initialization works perfectly
- - [x] File upload, validation, and preview functionality operational
- - [x] Backend processing, database storage, and file saving working
- - [x] Success message and page refresh after upload implemented
- - [x] Both upload paths working: main profile page and /profile/edit
- - [x] UUID-based file naming and security measures in place

### 🛡️ Enhanced Session Management

- - [x] Comprehensive session filtering (Active/Inactive/All)
- - [x] Enhanced UI with status tabs and visual indicators
- - [x] Session cleanup logic with scheduled tasks
- - [x] Session monitoring and analytics service
- - [x] Enhanced session tracking interceptor
- - [x] Configurable session parameters for all environments
- - [x] Fixed inactive session display issue (active=false sessions now visible)

### 🔐 2FA Authentication Flow Fixed

- - [x] Fixed LoginSuccessHandler to properly integrate 2FA with Remember Me logic
- - [x] Modified SecurityConfig to allow /auth/2fa access during authentication process
- - [x] Resolved HTTP 403 error when accessing 2FA verification page
- - [x] Implemented proper RememberMeAuthenticationToken detection
- - [x] 2FA now correctly required for fresh logins, skipped for Remember Me sessions

### 🖼️ Enhancement avatar functionality

- - [x] Add image preview before upload
- - [x] Add loading indicators
- - [x] Implement drag-and-drop upload
- - [x] Add loading indicators
- - [x] Support multiple image formats (PNG, GIF)
- - [x] Add image compression/resize capabilities

### 🔑 Implement password recovery

- - [x] Add password reset request form
- - [x] Email with reset link
- - [x] Secure token validation and password change
- - [x] Add a "Confirm new password" field to the password reset page for confirmation
- - [x] Add password complexity validation, same as on the user password change page
- - [x] JavaScript validation with real-time feedback and form control
- - [x] Proper Thymeleaf layout fragment integration
