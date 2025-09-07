package io.github.vibondarenko.clavionx.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.vibondarenko.clavionx.dto.UserCreateDto;
import io.github.vibondarenko.clavionx.entity.User;
import io.github.vibondarenko.clavionx.repository.UserRepository;
import io.github.vibondarenko.clavionx.security.Role;
import io.github.vibondarenko.clavionx.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final UserRepository userRepository;

    public UserAdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("pageTitle", "New User");
        model.addAttribute("pageDescription", "Add a new user to the system");
        model.addAttribute("pageIcon", "fa-plus-circle");

        model.addAttribute("userForm", new UserCreateDto());
        model.addAttribute("roles", Role.values());
        model.addAttribute("isEdit", false);
        return "admin/users/form";
    }

    @PostMapping("/new")
    public String createUser(@Valid @ModelAttribute("userForm") UserCreateDto userForm,
                            BindingResult bindingResult,
                            Model model,
                            jakarta.servlet.http.HttpServletRequest request) {
        if (userForm.getPassword() == null || userForm.getPassword().isBlank()) {
            bindingResult.rejectValue("password", "NotBlank", "Password is required");
        } else if (userForm.getPassword().length() < 6 || userForm.getPassword().length() > 64) {
            bindingResult.rejectValue("password", "Size", "Password must be between 6 and 64 characters");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("isEdit", false);
            return "admin/users/form";
        }
        try {
            String baseUrl = request.getScheme() + "://" + request.getServerName()
                    + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());
            userService.createUser(userForm, baseUrl);
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("error", ex.getMessage());
            return "admin/users/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        UserCreateDto dto = new UserCreateDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole().name());
        dto.setEnabled(user.isEnabled());

        model.addAttribute("pageTitle", "Edit User");
        model.addAttribute("pageDescription", "Edit user details");
        model.addAttribute("pageIcon", "fa-user-edit");

        model.addAttribute("userForm", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("userId", id);
        return "admin/users/form";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                            @Valid @ModelAttribute("userForm") UserCreateDto userForm,
                            BindingResult bindingResult,
                            Model model) {
        // Manual password validation for editing
        String password = userForm.getPassword();
        if (password != null && !password.isBlank()) {
            if (password.length() < 6 || password.length() > 64) {
                bindingResult.rejectValue("password", "Size", "Password must be between 6 and 64 characters");
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id);
            return "admin/users/form";
        }
        try {
            userService.updateUserFromDto(id, userForm);
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id);
            model.addAttribute("error", ex.getMessage());
            return "admin/users/form";
        }
    }

    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "User Details");
        model.addAttribute("pageDescription", "View user details");
        model.addAttribute("pageIcon", "fa-user");
        return "admin/users/view";
    }
}


