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

@Controller
public class ActivationController {
    private final ActivationTokenRepository activationTokenRepository;
    private final UserRepository userRepository;

    public ActivationController(ActivationTokenRepository activationTokenRepository, UserRepository userRepository) {
        this.activationTokenRepository = activationTokenRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/activate")
    public String activate(@RequestParam("token") String token, Model model) {
        Optional<ActivationToken> optToken = activationTokenRepository.findByToken(token);

        model.addAttribute("pageTitle", "Account Activation");
        model.addAttribute("pageDescription", "Your account has been successfully activated.");
        model.addAttribute("pageIcon", "fa-user-check");

        if (optToken.isEmpty()) {
            model.addAttribute("message", "Invalid or expired activation link.");
            return "activation/result";
        }
        ActivationToken activationToken = optToken.get();
        if (activationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            model.addAttribute("message", "Activation link has expired.");
            activationTokenRepository.delete(activationToken);
            return "activation/result";
        }
        User user = activationToken.getUser();
        if (user.isEnabled()) {
            model.addAttribute("message", "Account is already activated.");
            activationTokenRepository.delete(activationToken);
            return "activation/result";
        }
        user.setEnabled(true);
        userRepository.save(user);
        activationTokenRepository.delete(activationToken);

        model.addAttribute("message", "Account successfully activated. You can now log in.");
        return "activation/result";
    }
}



