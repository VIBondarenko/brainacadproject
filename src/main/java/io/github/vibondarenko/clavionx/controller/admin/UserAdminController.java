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
import io.github.vibondarenko.clavionx.view.ViewAttributes;
import jakarta.validation.Valid;

/**
 * Controller for managing users in the admin panel.
 * Provides endpoints for creating, editing, and viewing user details.
 * Uses UserService for business logic and UserRepository for data access.
 */
@Controller
@RequestMapping("/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Constructor for UserAdminController.
     *
     * @param userService    Service for user-related operations.
     * @param userRepository Repository for accessing user data.
     */
    public UserAdminController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }
    
    /**
     * Show the form for creating a new user.
     *
     * @param model Model to hold form attributes.
     * @return View name for the user creation form.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute(ViewAttributes.PAGE_TITLE, "New User");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Add a new user to the system");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-plus-circle");

        model.addAttribute("userForm", new UserCreateDto());
        model.addAttribute("roles", Role.values());
        model.addAttribute("isEdit", false);
        return "admin/users/form";
    }

    /**
     * Handle the submission of the user creation form.
     *
     * @param userForm      DTO containing user form data.
     * @param bindingResult Binding result for validation errors.
     * @param model         Model to hold attributes for the view.
     * @param request       HTTP servlet request for building base URL.
     * @return Redirect to user list on success, or back to form on error.
     */
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

    /**
     * Show the form for editing an existing user.
     *
     * @param id    ID of the user to edit.
     * @param model Model to hold form attributes.
     * @return View name for the user edit form.
     */
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

        model.addAttribute(ViewAttributes.PAGE_TITLE, "Edit User");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "Edit user details");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user-edit");

        model.addAttribute("userForm", dto);
        model.addAttribute("roles", Role.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("userId", id);
        return "admin/users/form";
    }

    /**
     * Handle the submission of the user edit form.
     *
     * @param id            ID of the user being edited.
     * @param userForm      DTO containing user form data.
     * @param bindingResult Binding result for validation errors.
     * @param model         Model to hold attributes for the view.
     * @return Redirect to user list on success, or back to form on error.
     */
    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                            @Valid @ModelAttribute("userForm") UserCreateDto userForm,
                            BindingResult bindingResult,
                            Model model) {
        // Manual password validation for editing
        String password = userForm.getPassword();
        if (password != null && !password.isBlank() && (password.length() < 6 || password.length() > 64)) {
            bindingResult.rejectValue("password", "Size", "Password must be between 6 and 64 characters");
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
    /**
     * View details of a specific user.
     *
     * @param id    ID of the user to view.
     * @param model Model to hold user attributes.
     * @return View name for displaying user details.
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        model.addAttribute("user", user);
        model.addAttribute(ViewAttributes.PAGE_TITLE, "User Details");
        model.addAttribute(ViewAttributes.PAGE_DESCRIPTION, "View user details");
        model.addAttribute(ViewAttributes.PAGE_ICON, "fa-user");
        return "admin/users/view";
    }
}