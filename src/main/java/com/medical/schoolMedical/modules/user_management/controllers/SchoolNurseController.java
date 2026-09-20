package com.medical.schoolMedical.modules.user_management.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nurse")
public class SchoolNurseController {
    @GetMapping("/nurse-home")
    public String nurseHome(Model model, Authentication authentication) {
        String username = authentication.getName(); // Lấy tên người dùng
        model.addAttribute("username", username);
        return "nurse/nurse-home"; // templates/nurse/home.html
    }
}
