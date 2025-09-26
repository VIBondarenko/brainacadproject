package io.github.vibondarenko.clavionx.security;

/**
 * Common application paths used by Spring Security matchers.
 */
public final class Paths {

    public static final String ROOT                         = "/";
    public static final String LOGIN                        = "/login";
    public static final String LOGIN_ERROR                  = "/login?error=true";    
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
    public static final String AUTH_FORGOT_PASSWORD         = "/auth/forgot-password";
    public static final String AUTH_RESET_PASSWORD          = "/auth/reset-password";

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
    public static final String COURSES_LIST                 = "/courses/list";
    public static final String COURSES_FORM                 = "/courses/form";
    public static final String COURSES_NEW                  = "/courses/new";
    public static final String COURSES_EDIT                 = "/courses/%d/edit";
    public static final String COURSES_VIEW                 = "/courses/view";

    public static final String PROFILE                      = "/profile";
    public static final String PROFILE_EDIT                 = "/profile/edit";
    public static final String PROFILE_CHANGE_PASSWORD      = "/profile/change-password";

    public static final String SETTINGS                     = "/settings";
    public static final String SETTINGS_2FA                 = "/settings/2fa";
    public static final String SETTINGS_2FA_ENABLE          = "/settings/2fa/enable";
    public static final String SETTINGS_2FA_VERIFY          = "/settings/2fa/verify";
    public static final String SETTINGS_2FAVERIFY           = "/settings/2fa-verify";

    public static final String ACTIVATION_RESULT            = "/activation/result";
    public static final String ADMIN_USERS_FORM             = "/admin/users/form";
    public static final String ADMIN_USERS_VIEW             = "admin/users/view";

    // Redirects
    public static final String REDIRECT_DASHBOARD           = "redirect:/dashboard";
    public static final String REDIRECT_ADMIN_SESSIONS      = "redirect:/admin/sessions";

    public static final String REDIRECT_COURSES             = "redirect:/courses";
    public static final String REDIRECT_COURSES_NEW         = "redirect:/courses/new";
    public static final String REDIRECT_COURSES_EDIT        = "redirect:/courses/%d/edit";

    public static final String REDIRECT_LOGIN               = "redirect:/login";
    public static final String REDIRECT_LOGIN_ERROR         = "redirect:/login?error=true";

    public static final String REDIRECT_PROFILE             = "redirect:/profile";

    public static final String REDIRECT_SETTINGS            = "redirect:/settings";
    public static final String REDIRECT_SETTINGS_2FA        = "redirect:/settings/2fa";
    public static final String REDIRECT_SETTINGS_2FA_ENABLE = "redirect:/settings/2fa/enable";
    public static final String REDIRECT_SETTINGS_2FA_VERIFY = "redirect:/settings/2fa/verify";
    public static final String REDIRECT_SETTINGS_2FAVERIFY  = "redirect:/settings/2fa-verify";

    public static final String REDIRECT_AUTH_2FA            = "redirect:/auth/2fa";
    public static final String REDIRECT_AUTH_2FA_ERROR      = "redirect:/auth/2fa?error=true";

    public static final String REDIRECT_ADMIN_USERS         = "redirect:/admin/users";

    protected static final String[] PUBLIC = {
        ROOT, LOGIN, ACTIVATE, REGISTER, CSS_ALL, JS_ALL, IMAGES_ALL, FAVICON, FORGOT_PASSWORD, RESET_PASSWORD
    };

    private Paths() {}

    public static String[] getPublic() {
        return PUBLIC;
    }

}