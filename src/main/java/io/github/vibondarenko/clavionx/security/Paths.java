package io.github.vibondarenko.clavionx.security;

/**
 * Common application paths used by Spring Security matchers.
 */
public final class Paths {

    public static final String ROOT                 = "/";
    public static final String LOGIN                = "/login";
    public static final String ACTIVATE             = "/activate";
    public static final String REGISTER             = "/register";
    public static final String FORGOT_PASSWORD      = "/forgot-password";
    public static final String RESET_PASSWORD       = "/reset-password";
    public static final String CSS                  = "/css/**";
    public static final String JS                   = "/js/**";
    public static final String IMAGES               = "/images/**";
    public static final String FAVICON              = "/favicon.ico";

    public static final String AUTH_2FA             = "/auth/2fa";
    public static final String AUTH_2FA_ALL         = "/auth/2fa/**";
    public static final String SETTINGS_2FA_ALL     = "/settings/2fa/**";

    public static final String DASHBOARD_ALL        = "/dashboard/**";
    public static final String ADMIN_ALL            = "/admin/**";
    public static final String MANAGE_ALL           = "/manage/**";
    public static final String COURSES_ALL          = "/courses/**";
    public static final String COURSES_MANAGE_ALL   = "/courses/manage/**";
    public static final String STUDENT_ALL          = "/student/**";
    public static final String ANALYTICS_ALL        = "/analytics/**";
    public static final String REPORTS_ALL          = "/reports/**";

    public static final String API_PUBLIC           = "/api/public/**";
    public static final String API_ADMIN_ALL        = "/api/admin/**";
    public static final String API_MANAGE_ALL       = "/api/manage/**";

    public static final String REDIRECT_ADMIN_SESSIONS       = "redirect:/admin/sessions";
    public static final String REDIRECT_COURSES              = "redirect:/courses";
    public static final String REDIRECT_COURSES_NEW          = "redirect:/courses/new";
    public static final String REDIRECT_COURSES_EDIT         = REDIRECT_COURSES + "/%d/edit";
    

    protected static final String[] PUBLIC = {
        ROOT, LOGIN, ACTIVATE, REGISTER, CSS, JS, IMAGES, FAVICON, FORGOT_PASSWORD, RESET_PASSWORD
    };

    private Paths() {}

    public static String[] getPublic() {
        return PUBLIC;
    }

}
