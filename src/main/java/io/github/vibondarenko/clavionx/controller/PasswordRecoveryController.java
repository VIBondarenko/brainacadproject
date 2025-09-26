package io.github.vibondarenko.clavionx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.vibondarenko.clavionx.security.Paths;
import io.github.vibondarenko.clavionx.service.PasswordResetService;
import io.github.vibondarenko.clavionx.view.ViewAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Controller for handling password recovery processes including
 * displaying forms and processing submissions for forgotten passwords
 * and password resets.
 */
@Controller
public class PasswordRecoveryController {

    private final PasswordResetService passwordResetService;

    /**
     * Constructs a PasswordRecoveryController with the specified PasswordResetService.
     *
     * @param passwordResetService the service for handling password reset logic
     */
    public PasswordRecoveryController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * Displays the forgot password form.
     *
     * @param model the model to hold attributes for the view
     * @return the name of the forgot password view
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        setForgotPasswordViewAttributes(model);

        model.addAttribute("emailForm", new EmailForm());
        return Paths.AUTH_FORGOT_PASSWORD;
    }

    private void setForgotPasswordViewAttributes(Model model) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Reset Password");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Reset your password");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-lock");
    }

    /**
     * Processes the submission of the forgot password form.
     *
     * @param form the form containing the user's email
     * @param bindingResult the result of binding the form
     * @param model the model to hold attributes for the view
     * @param request the HTTP request to construct the base URL
     * @return the name of the forgot password view
     */
    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("emailForm") EmailForm form,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request) {
        setForgotPasswordViewAttributes(model);

        if (!bindingResult.hasErrors()) {
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
            passwordResetService.sendResetLink(form.getEmail(), baseUrl);
            model.addAttribute("message", "If this email exists, a reset link has been sent.");
        }

        return Paths.AUTH_FORGOT_PASSWORD;
    }

    /**
     * Displays the reset password form.
     *
     * @param token the password reset token
     * @param model the model to hold attributes for the view
     * @return the name of the reset password view
     */
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        setForgotPasswordViewAttributes(model);

        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "Invalid or expired token.");
        } else {
            model.addAttribute("token", token);
            model.addAttribute("passwordForm", new PasswordForm());
        }
        return Paths.AUTH_RESET_PASSWORD;
    }

    /**
     * Processes the submission of the reset password form.
     *
     * @param token the password reset token
     * @param form the form containing the new password and confirmation
     * @param bindingResult the result of binding the form
     * @param model the model to hold attributes for the view
     * @return the name of the reset password view
     */
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                        @Valid @ModelAttribute("passwordForm") PasswordForm form,
                                        BindingResult bindingResult,
                                        Model model) {
        setForgotPasswordViewAttributes(model);

        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
        } else {
            if (!form.getPassword().equals(form.getConfirmPassword())) {
                model.addAttribute("token", token);
                model.addAttribute("error", "Passwords do not match.");
            } else {
                boolean success = passwordResetService.resetPassword(token, form.getPassword());
                if (!success) {
                    model.addAttribute("error", "Invalid or expired token.");
                } else {
                    model.addAttribute("message", "Password successfully changed. You can now log in.");
                }
            }
        }
        return Paths.AUTH_RESET_PASSWORD;
    }

    /**
     * Form backing object for email input in the forgot password process.
     */
    public static class EmailForm {
        @NotBlank
        @Email
        private String email;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
    }
    /**
     * Form backing object for password input in the reset password process.
     */
    public static class PasswordForm {
        @NotBlank
        private String password;

        @NotBlank
        private String confirmPassword;

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }
        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}