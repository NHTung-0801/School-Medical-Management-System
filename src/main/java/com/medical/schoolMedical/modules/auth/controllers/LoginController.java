package com.medical.schoolMedical.modules.auth.controllers;

import com.medical.schoolMedical.modules.user_management.dto.UserDTO;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return redirectToHome(auth);
        }

        model.addAttribute("user", new UserDTO());
        if (error != null) {
            model.addAttribute("errorMessage", "Sai tên đăng nhập hoặc mật khẩu!");
        }
        return "user/login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return redirectToHome(auth);
        }

        return "admin/login";  // View ở: templates/admin/login.html
    }

    private String redirectToHome(Authentication auth) {
        for (GrantedAuthority ga : auth.getAuthorities()) {
            String role = ga.getAuthority();
            if ("ROLE_ADMIN".equals(role)) {
                return "redirect:/admin/dashboard";
            } else if ("ROLE_NURSE".equals(role)) {
                return "redirect:/nurse/nurse-home";
            } else if ("ROLE_PARENT".equals(role)) {
                return "redirect:/parent/parent-home";
            } else if ("ROLE_MANAGER".equals(role)) {
                return "redirect:/manager/manager-home";
            }
        }
        return "redirect:/";
    }
}
