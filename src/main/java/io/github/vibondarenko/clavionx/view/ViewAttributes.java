package io.github.vibondarenko.clavionx.view;

/**
 * Common view/model attribute keys used across controllers.
 */
public final class ViewAttributes {
    private ViewAttributes() {}

    public static final String PAGE_TITLE = "pageTitle";
    public static final String PAGE_DESCRIPTION = "pageDescription";
    public static final String PAGE_ICON = "pageIcon";

    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String ERROR_MESSAGE = "errorMessage";

    public static final class Redirects {
        private Redirects() {}
        public static final String ADMIN_SESSIONS = "redirect:/admin/sessions";
    }
}
