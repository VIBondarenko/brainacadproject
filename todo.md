# TODO - Task List for Improvements

📌 **What can still be improved:**

- ~~Add real email field to User entity~~ ✅ **Completed**
- ~~Implement session tracking system~~ ✅ **COMPLETED**
  - ✅ Created UserSession entity
  - ✅ Created UserSessionRepository  
  - ✅ Created SessionService
  - ✅ Created SessionTrackingInterceptor
  - ✅ Integration with authentication (LoginSuccessHandler, CustomLogoutSuccessHandler)
  - ✅ Web interface for session management (AdminController + HTML templates)
  - ✅ Testing - application starts without errors
  - ✅ DB migration - user_sessions table created automatically
- ~~Translate all Russian text to English~~ ✅ **COMPLETED**
  - ✅ Translated web interface elements (admin dashboard, session management, course views)
  - ✅ Translated Java code comments and JavaDoc
  - ✅ Updated all user-facing text in HTML templates
- ~~Improve button visibility on Security Monitoring page~~ ✅ **COMPLETED**
  - ✅ Enhanced CSS styles for better text contrast
  - ✅ Fixed "Activities" and "Analytics" buttons in orange header
  - ✅ Improved "Investigate" button readability
  - ✅ Enhanced "Cleanup Old Data" button visibility
- ~~Reorganize Spring Boot configuration profiles~~ ✅ **COMPLETED**
  - ✅ Cleaned up application.yml (removed duplicate profile configurations)
  - ✅ Fixed application-dev.yml (PostgreSQL for development - same as default)
  - ✅ Created application-prod.yml (PostgreSQL for production with optimized settings)
  - ✅ Proper separation of concerns between environments
- ~~Add .env file support for secure environment variables~~ ✅ **COMPLETED**
  - ✅ Added dotenv-java dependency to pom.xml
  - ✅ Created DotEnvConfig class for loading .env files
  - ✅ Configured auto-initialization via spring.factories
  - ✅ Updated application configurations to use environment variables
  - ✅ All passwords now loaded securely from .env file
- ~~Update navigation menu structure~~ ✅ **COMPLETED**
  - ✅ Removed "Activities" from main navigation menu
  - ✅ Restricted "Activity History" access to SuperAdmin and Admin only
  - ✅ Improved security and UX design
- ~~Add profile editing capability~~ ✅ **COMPLETED**
  - ✅ Created UserProfileDto and PasswordChangeDto for data validation
  - ✅ Implemented ProfileService with transaction support
  - ✅ Added ProfileController endpoints (edit, change-password, upload-avatar)
  - ✅ Created responsive HTML templates for profile editing
  - ✅ Added UserRepository.findByEmail() and existsByEmail() methods
  - ✅ Integrated edit links into existing profile page
  - ✅ Added PASSWORD_CHANGE activity type to UserActivityService

## 🎯 **Next Priority Tasks:**

1. **✅ Avatar upload functionality - COMPLETED!**
   - ✅ Modal window opens correctly on main profile page (/profile)
   - ✅ JavaScript initialization works perfectly
   - ✅ File upload, validation, and preview functionality operational
   - ✅ Backend processing, database storage, and file saving working
   - ✅ Success message and page refresh after upload implemented
   - ✅ Both upload paths working: main profile page and /profile/edit
   - ✅ UUID-based file naming and security measures in place
   
2. **Enhancement avatar functionality**
2. **Enhancement avatar functionality**
   - Add image preview before upload
   - Implement drag-and-drop upload
   - Add loading indicators
   - Support multiple image formats (PNG, GIF)
   - Add image compression/resize capabilities

3. **Implement real user activity history**
   - Enhanced activity tracking with more detail
   - Activity filtering and search
   - Export functionality for audit reports

4. **Add notification system**
   - Email notifications for important events
   - In-app notification center
   - Customizable notification preferences

5. **Improve course management**
   - Course materials upload
   - Assignment submission system
   - Grade tracking

6. **Add reporting dashboard**
   - Student progress reports
   - Course completion statistics
   - Custom report generation

7. **Enhance security features**
   - Two-factor authentication (2FA)
   - Password policy enforcement
   - Account lockout protection
