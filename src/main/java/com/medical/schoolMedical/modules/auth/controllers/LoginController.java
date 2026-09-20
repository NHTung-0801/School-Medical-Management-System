package com.medical.schoolMedical.modules.auth.controllers;

import com.medical.schoolMedical.modules.user_management.dto.UserDTO;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Model model) {
        model.addAttribute("user", new UserDTO());
        if (error != null) {
            model.addAttribute("errorMessage", "Sai tên đăng nhập hoặc mật khẩu!");
        }
        return "user/login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage(Model model) {
        return "admin/login";  // View ở: templates/admin/login.html
    }
}
