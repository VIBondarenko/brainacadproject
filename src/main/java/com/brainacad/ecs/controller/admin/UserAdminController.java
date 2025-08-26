package com.brainacad.ecs.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brainacad.ecs.dto.UserCreateDto;
import com.brainacad.ecs.entity.User;
import com.brainacad.ecs.repository.UserRepository;
import com.brainacad.ecs.security.Role;
import com.brainacad.ecs.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public UserAdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        model.addAttribute("roles", Role.values());
        return "admin/users/create";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute("userCreateDto") UserCreateDto userCreateDto,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/users/create";
        }
        try {
            User user = userService.createUser(userCreateDto);
            model.addAttribute("success", "User created successfully");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("error", ex.getMessage());
            return "admin/users/create";
        }
    }
}
