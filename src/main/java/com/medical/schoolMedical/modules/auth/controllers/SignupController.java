package com.medical.schoolMedical.modules.auth.controllers;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.user_management.dto.UserDTO;
import com.medical.schoolMedical.modules.user_management.entities.User;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class SignupController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/add-user")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", Role.values());
        return "admin/add-user";
    }

    @PostMapping("/add-user")
    public String addUser(@ModelAttribute("user") @Valid UserDTO user,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", Role.values());
            return "admin/add-user";
        }
        // kiểm tra dữ liệu đầu vào
        try {
            userService.validateUserInput(user);
        } catch (BusinessException ex) {
            model.addAttribute("roles", Role.values());
            bindingResult.rejectValue("password", null, ex.getMessage());
            return "admin/add-user";
        }
        // Lưu user
        try {
            userService.signUp(user);
            redirectAttributes.addFlashAttribute("success", "Đăng kí người dùng thành công");
            return "redirect:/admin/manage-users";
        } catch (BusinessException ex) {
            model.addAttribute("roles", Role.values());
            bindingResult.rejectValue("username", null, ex.getMessage());
            return "admin/add-user";
        }
    }
}
