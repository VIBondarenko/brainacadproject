package io.github.vibondarenko.clavionx.security;

/**
 * Common application paths used by Spring Security matchers.
 */
public final class Paths {

    public static final String ROOT                         = "/";
    public static final String LOGIN                        = "/login";
    public static final String ACTIVATE                     = "/activate";
    public static final String REGISTER                     = "/register";
    public static final String FORGOT_PASSWORD              = "/forgot-password";
    public static final String RESET_PASSWORD               = "/reset-password";
    
    public static final String CSS_ALL                      = "/css/**";
    public static final String JS_ALL                       = "/js/**";
    public static final String IMAGES_ALL                   = "/images/**";

    public static final String FAVICON                      = "/favicon.ico";
    public static final String LOGOUT                       = "/logout";

    public static final String AUTH                         = "/auth";
    public static final String AUTH_ALL                     = "/auth/**";

    public static final String AUTH_2FA                     = "/auth/2fa";
    public static final String AUTH_2FA_ALL                 = "/auth/2fa/**";
    public static final String SETTINGS_2FA_ALL             = "/settings/2fa/**";

    public static final String DASHBOARD_ALL                = "/dashboard/**";
    public static final String ADMIN_ALL                    = "/admin/**";
    public static final String MANAGE_ALL                   = "/manage/**";
    public static final String COURSES_ALL                  = "/courses/**";
    public static final String COURSES_MANAGE_ALL           = "/courses/manage/**";
    public static final String STUDENT_ALL                  = "/student/**";
    public static final String ANALYTICS_ALL                = "/analytics/**";
    public static final String REPORTS_ALL                  = "/reports/**";

    public static final String API_PUBLIC                   = "/api/public/**";
    public static final String API_ADMIN_ALL                = "/api/admin/**";
    public static final String API_MANAGE_ALL               = "/api/manage/**";

    public static final String DASHBOARD                    = "/dashboard";
    public static final String ADMIN_SESSIONS               = "/admin/sessions";
    public static final String COURSES                      = "/courses";
    public static final String COURSES_NEW                  = "/courses/new";

    public static final String PROFILE                      = "/profile";
    public static final String SETTINGS                     = "/settings";
    
    public static final String SETTINGS_2FA                 = "/settings/2fa";
    public static final String SETTINGS_2FA_ENABLE          = "/settings/2fa/enable";
    public static final String SETTINGS_2FA_VERIFY          = "/settings/2fa/verify";
    public static final String SETTINGS_2FAVERIFY           = "/settings/2fa-verify";

    public static final String Q_ERROR_TRUE                 = "?error=true";

    public static final String REDIRECT_ROOT                = "redirect:";
    public static final String REDIRECT_ADMIN_SESSIONS      = REDIRECT_ROOT + ADMIN_SESSIONS;
    public static final String REDIRECT_COURSES             = REDIRECT_ROOT + COURSES;
    public static final String REDIRECT_COURSES_NEW         = REDIRECT_ROOT + COURSES_NEW;
    public static final String REDIRECT_COURSES_EDIT        = REDIRECT_COURSES + "/%d/edit";
    public static final String REDIRECT_DASHBOARD           = REDIRECT_ROOT + DASHBOARD;

    public static final String LOGIN_ERROR                  = LOGIN + Q_ERROR_TRUE;

    public static final String REDIRECT_LOGIN               = REDIRECT_ROOT + LOGIN;
    public static final String REDIRECT_LOGIN_ERROR         = REDIRECT_LOGIN + Q_ERROR_TRUE;
    public static final String REDIRECT_PROFILE             = REDIRECT_ROOT + PROFILE;
    public static final String REDIRECT_SETTINGS            = REDIRECT_ROOT + SETTINGS;

    public static final String REDIRECT_SETTINGS_2FA        = REDIRECT_ROOT + SETTINGS_2FA;
    public static final String REDIRECT_SETTINGS_2FA_ENABLE = REDIRECT_ROOT + SETTINGS_2FA_ENABLE;
    public static final String REDIRECT_SETTINGS_2FA_VERIFY = REDIRECT_ROOT + SETTINGS_2FA_VERIFY;
    public static final String REDIRECT_SETTINGS_2FAVERIFY  = REDIRECT_ROOT + SETTINGS_2FAVERIFY;

    public static final String REDIRECT_AUTH_2FA            = REDIRECT_ROOT + AUTH_2FA;
    public static final String REDIRECT_AUTH_2FA_ERROR      = REDIRECT_AUTH_2FA + Q_ERROR_TRUE;


    protected static final String[] PUBLIC = {
        ROOT, LOGIN, ACTIVATE, REGISTER, CSS_ALL, JS_ALL, IMAGES_ALL, FAVICON, FORGOT_PASSWORD, RESET_PASSWORD
    };

    private Paths() {}

    public static String[] getPublic() {
        return PUBLIC;
    }

}
