package io.github.vibondarenko.clavionx.controller.admin;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class UserListAdminController {
    private final UserRepository userRepository;

    public UserListAdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("pageTitle", "Users List");
        model.addAttribute("pageDescription", "Manage all users in the system");
        model.addAttribute("pageIcon", "fa-users");
        
        model.addAttribute("users", users);
        return "admin/users/list";
    }
}



