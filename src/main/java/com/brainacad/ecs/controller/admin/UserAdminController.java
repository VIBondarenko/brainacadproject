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

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        model.addAttribute("roles", Role.values());
        return "admin/users/form";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("userCreateDto") UserCreateDto userCreateDto,
                             BindingResult bindingResult,
                             Model model,
                             jakarta.servlet.http.HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/users/form";
        }
        try {
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());
            User user = userService.createUser(userCreateDto, baseUrl);
            model.addAttribute("success", "User created successfully");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("error", ex.getMessage());
            return "admin/users/form";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "admin/users/form";
    }

    @PostMapping("/edit")
    public String editUser(@Valid @ModelAttribute("user") User user,
                            BindingResult bindingResult,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/users/form";
        }
        try {
            userService.updateUser(user);
            model.addAttribute("success", "User updated successfully");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("error", ex.getMessage());
            return "admin/users/form";
        }
    }
}