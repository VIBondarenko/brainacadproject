package io.github.vibondarenko.clavionx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for About page
 */
@Controller
public class AboutController {

    /**
     * Handles GET requests for the About page
     *
     * @param model Model to pass attributes to the view
     * @return The name of the view template for the About page
     */
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About");
        model.addAttribute("pageDescription", "About ClavionX Application");
        model.addAttribute("pageIcon", "fa-info-circle");
        return "pages/about";
    }
}