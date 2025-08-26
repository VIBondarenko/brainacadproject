package com.brainacad.ecs.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;

@Controller
@RequestMapping("/admin/users")
public class UserListAdminController {
    private final UserRepository userRepository;

    @Autowired
    public UserListAdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public String listUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users/list";
    }
}
