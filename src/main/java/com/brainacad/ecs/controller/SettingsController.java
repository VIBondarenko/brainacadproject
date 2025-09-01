package com.brainacad.ecs.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for user settings management
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

    /**
     * Display settings page
     */
    @GetMapping
    public String settings(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("pageTitle", "Settings");
            model.addAttribute("pageDescription", "Manage your account preferences and application settings");
            model.addAttribute("pageIcon", "fa-cog");
                        
            // Add user info to model for future use
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authorities", authentication.getAuthorities());
            
            return "settings/index";
        } else {
            return "redirect:/login";
        }
    }
}
