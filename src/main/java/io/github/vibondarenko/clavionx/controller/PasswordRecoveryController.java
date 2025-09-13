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

@Controller
public class PasswordRecoveryController {

    private final PasswordResetService passwordResetService;

    public PasswordRecoveryController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Forgot Password");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Reset your password");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-lock");

        model.addAttribute("emailForm", new EmailForm());
        return Paths.AUTH_FORGOT_PASSWORD;
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("emailForm") EmailForm form,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Forgot Password");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Reset your password");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-lock");

        if (!bindingResult.hasErrors()) {
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
            passwordResetService.sendResetLink(form.getEmail(), baseUrl);
            model.addAttribute("message", "If this email exists, a reset link has been sent.");
        }

        return Paths.AUTH_FORGOT_PASSWORD;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Reset Password");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Reset your password");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-lock");

        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "Invalid or expired token.");
        } else {
            model.addAttribute("token", token);
            model.addAttribute("passwordForm", new PasswordForm());
        }
        return Paths.AUTH_RESET_PASSWORD;
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                        @Valid @ModelAttribute("passwordForm") PasswordForm form,
                                        BindingResult bindingResult,
                                        Model model) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "Reset Password");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Reset your password");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-lock");

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



