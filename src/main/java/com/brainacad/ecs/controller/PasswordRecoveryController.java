package com.brainacad.ecs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.brainacad.ecs.service.PasswordResetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Controller
public class PasswordRecoveryController {

    @Autowired
    private PasswordResetService passwordResetService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("emailForm", new EmailForm());
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@Valid @ModelAttribute("emailForm") EmailForm form,
                                        BindingResult bindingResult,
                                        Model model,
                                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        String baseUrl = request.getScheme() + "://" + request.getServerName()
                + (request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort());
        passwordResetService.sendResetLink(form.getEmail(), baseUrl);
        model.addAttribute("message", "If this email exists, a reset link has been sent.");
        return "auth/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.isValidToken(token)) {
            model.addAttribute("error", "Invalid or expired token.");
            return "auth/reset-password";
        }
        model.addAttribute("token", token);
        model.addAttribute("passwordForm", new PasswordForm());
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                        @Valid @ModelAttribute("passwordForm") PasswordForm form,
                                        BindingResult bindingResult,
                                        Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
            return "auth/reset-password";
        }
        boolean success = passwordResetService.resetPassword(token, form.getPassword());
        if (!success) {
            model.addAttribute("error", "Invalid or expired token.");
            return "auth/reset-password";
        }
        model.addAttribute("message", "Password successfully changed. You can now log in.");
        return "auth/reset-password";
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

        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
}
