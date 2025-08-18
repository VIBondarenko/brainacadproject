# TODO - Task List for Improvements

📌 **What can still be improved:**

- ~~Add real email field to User entity~~ ✅ Completed
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
- Add real user activity history
- Add profile editing capability
