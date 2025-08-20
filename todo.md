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
- Add real user activity history
- Add profile editing capability
