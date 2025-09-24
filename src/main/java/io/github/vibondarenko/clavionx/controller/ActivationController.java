package io.github.vibondarenko.clavionx.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.vibondarenko.clavionx.entity.ActivationToken;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.ActivationTokenRepository;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Paths;

/**
 * Controller to handle account activation via activation tokens.
 */
@Controller
public class ActivationController {
    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param activationTokenRepository Repository for activation tokens.
     * @param userRepository            Repository for users.
     */
    public ActivationController(ActivationTokenRepository activationTokenRepository, UserRepository userRepository) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * Handles account activation requests.
     *
     * @param token Activation token from the request parameter.
     * @param model Model to pass attributes to the view.
     * @return The name of the view to render or a redirect string.
     */
    @GetMapping("/activate")
    public String activate(@RequestParam("token") String token, Model model) {
        Optional<ActivationToken> optToken = activationTokenRepository.findByToken(token);

        model.addAttribute("pageTitle", "Account Activation");
        model.addAttribute("pageDescription", "Your account has been successfully activated.");
        model.addAttribute("pageIcon", "fa-user-check");

        if (optToken.isEmpty()) {
            model.addAttribute("message", "Invalid or expired activation link.");
            return Paths.ACTIVATION_RESULT;
        }
        ActivationToken activationToken = optToken.get();
        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Activation link has expired.");
            activationTokenRepository.delete(activationToken);
            return Paths.ACTIVATION_RESULT;
        }
        User user = activationToken.getUser();
        if (user.isEnabled()) {
            model.addAttribute("message", "Account is already activated.");
            activationTokenRepository.delete(activationToken);
            return Paths.ACTIVATION_RESULT;
        }
        user.setEnabled(true);
        userRepository.save(user);
        activationTokenRepository.delete(activationToken);

        // Redirect to login page upon successful activation for better UX
        return "redirect:/login?activated";
    }
}