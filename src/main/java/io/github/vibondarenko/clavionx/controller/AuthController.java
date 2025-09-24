package io.github.vibondarenko.clavionx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for authentication pages
 */
@Controller
public class AuthController {
    
    /**
     * Handles GET requests for the login page.
     * 
     * @param error   Optional error message parameter
     * @param logout  Optional logout message parameter
     * @param model   Model to pass attributes to the view
     * @return        The name of the login view template
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password!");
        }
        
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        
        return "login";
    }
    
    /**
     * Redirects the root URL to the dashboard.
     * 
     * @return A redirect string to the dashboard
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }
}