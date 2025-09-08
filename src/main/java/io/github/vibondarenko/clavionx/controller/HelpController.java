package io.github.vibondarenko.clavionx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for Help page
 */
@Controller
public class HelpController {

    @GetMapping("/help")
    public String help(Model model) {
        model.addAttribute("pageTitle", "Help");
        model.addAttribute("pageDescription", "Help and Support for ClavionX");
        model.addAttribute("pageIcon", "fa-question-circle");
        return "pages/help";
    }
}
