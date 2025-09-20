package io.github.vibondarenko.clavionx.controller.admin;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.entity.UserLoginSecurity;
import io.github.vibondarenko.clavionx.repository.UserLoginSecurityRepository;
import io.github.vibondarenko.clavionx.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class UserListAdminController {
    private final UserRepository userRepository;
    private final UserLoginSecurityRepository securityRepository;

    public UserListAdminController(UserRepository userRepository,
                                   UserLoginSecurityRepository securityRepository) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
    }

    @GetMapping("")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        // Map of userId -> lockedUntil (only if still locked)
        Map<Long, LocalDateTime> lockedUntil = users.stream()
            .map(u -> securityRepository.findByUser(u).orElse(null))
            .filter(sec -> sec != null && sec.getLockedUntil() != null && now.isBefore(sec.getLockedUntil()))
            .collect(Collectors.toMap(sec -> sec.getUser().getId(), UserLoginSecurity::getLockedUntil));
        model.addAttribute("pageTitle", "Users List");
        model.addAttribute("pageDescription", "Manage all users in the system");
        model.addAttribute("pageIcon", "fa-users");
        
        model.addAttribute("users", users);
        model.addAttribute("lockedUntil", lockedUntil);
        return "admin/users/list";
    }
}



