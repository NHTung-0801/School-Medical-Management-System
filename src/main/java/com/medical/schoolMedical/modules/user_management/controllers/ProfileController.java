package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.modules.user_management.entities.*;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.util.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    @Autowired
    private UserService userService;

    // Điều hướng thông minh /profile về đúng trang hồ sơ theo vai trò
    @GetMapping("/profile")
    public String redirectProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null || customUserDetails.getUser() == null) {
            return "redirect:/login";
        }
        Role role = customUserDetails.getUser().getRole();
        if (role == null) {
            return "redirect:/login";
        }
        return switch (role) {
            case ADMIN -> "redirect:/admin/profile";
            case NURSE -> "redirect:/nurse/profile";
            case PARENT -> "redirect:/parent/profile";
            case MANAGER -> "redirect:/manager/profile";
        };
    }

    @GetMapping("/edit-profile")
    public String redirectEditProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null || customUserDetails.getUser() == null) {
            return "redirect:/login";
        }
        return "redirect:/" + customUserDetails.getUser().getRole().name().toLowerCase() + "/edit-profile";
    }

    // Trang thông tin cá nhân theo vai trò
    @GetMapping({"/admin/profile", "/parent/profile", "/manager/profile", "/nurse/profile"})
    public String viewProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                              Model model) {

        if (customUserDetails == null || customUserDetails.getUser() == null) {
            return "redirect:/login";
        }

        String username = customUserDetails.getUser().getUsername();
        User user = userService.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);

        Role role = user.getRole();
        if (role == null) {
            return "redirect:/login";
        }

        String homeUrl = switch (role) {
            case ADMIN -> "/admin/dashboard";
            case PARENT -> "/parent/parent-home";
            case MANAGER -> "/manager/manager-home";
            case NURSE -> "/nurse/nurse-home";
        };
        model.addAttribute("homeUrl", homeUrl);

        switch (role) {
            case ADMIN -> {
                Admin admin = userService.findAdminByUsername(username);
                model.addAttribute("info", admin);
            }
            case PARENT -> {
                Parent parent = userService.findParentByUsername(username);
                model.addAttribute("info", parent);
            }
            case MANAGER -> {
                Manager manager = userService.findManagerByUsername(username);
                model.addAttribute("info", manager);
            }
            case NURSE -> {
                SchoolNurse nurse = userService.findNurseByUsername(username);
                model.addAttribute("info", nurse);
            }
        }

        return "profile/profile"; // Giao diện chung
    }

    @GetMapping({"/admin/edit-profile", "/parent/edit-profile", "/manager/edit-profile", "/nurse/edit-profile"})
    public String showEditProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  Model model) {
        if (customUserDetails == null || customUserDetails.getUser() == null) {
            return "redirect:/login";
        }
        User user = customUserDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("role", customUserDetails.getUser().getRole().name().toLowerCase());

        switch (user.getRole()) {
            case ADMIN -> model.addAttribute("info", userService.findAdminByUsername(user.getUsername()));
            case PARENT -> model.addAttribute("info", userService.findParentByUsername(user.getUsername()));
            case MANAGER -> model.addAttribute("info", userService.findManagerByUsername(user.getUsername()));
            case NURSE -> model.addAttribute("info", userService.findNurseByUsername(user.getUsername()));
        }

        return "profile/edit-profile";
    }

    @PostMapping({"/admin/update-profile", "/parent/update-profile", "/manager/update-profile", "/nurse/update-profile"})
    public String updateProfile(@RequestParam long userId,
                                @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {

        Role role = customUserDetails.getUser().getRole();
        User user = userService.findById(userId); // đã đảm bảo tồn tại

        String email = request.getParameter("email");

        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }

        String rawFullName = request.getParameter("fullName");
        String fullName = ValidationUtil.normalizeFullName(rawFullName);
        if (rawFullName != null && !rawFullName.isBlank() && !ValidationUtil.isValidFullName(fullName)) {
            redirectAttributes.addFlashAttribute("error", "Họ và tên không hợp lệ. Vui lòng chỉ nhập chữ cái và khoảng trắng, không chứa ký tự đặc biệt (?, #...).");
            return "redirect:/" + role.name().toLowerCase() + "/edit-profile";
        }

        switch (role) {
            case ADMIN -> {
                Admin admin = userService.findAdminByUsername(user.getUsername());
                if (admin == null) {
                    admin = new Admin();
                    admin.setUser(user);
                }
                admin.setFullName(fullName);
                userService.saveAdmin(admin);
            }

            case PARENT -> {
                Parent parent = userService.findParentByUsername(user.getUsername());
                if (parent == null) {
                    parent = new Parent();
                    parent.setUser(user);
                }
                parent.setFullName(fullName);
                parent.setPhoneNumber(request.getParameter("phoneNumber"));
                parent.setAddress(request.getParameter("address"));
                userService.saveParent(parent);
            }

            case MANAGER -> {
                Manager manager = userService.findManagerByUsername(user.getUsername());
                if (manager == null) {
                    manager = new Manager();
                    manager.setUser(user);
                }
                manager.setFullName(fullName);
                userService.saveManager(manager);
            }

            case NURSE -> {
                SchoolNurse nurse = userService.findNurseByUsername(user.getUsername());
                if (nurse == null) {
                    nurse = new SchoolNurse();
                    nurse.setUser(user);
                }
                nurse.setFullName(fullName);

                String experienceParam = request.getParameter("experience");
                if (experienceParam != null && !experienceParam.isBlank()) {
                    try {
                        nurse.setExperience(Integer.parseInt(experienceParam));
                    } catch (NumberFormatException e) {
                        redirectAttributes.addFlashAttribute("error", "Kinh nghiệm phải là số nguyên!");
                        return "redirect:/" + role.name().toLowerCase() + "/edit-profile";
                    }
                }

                userService.saveNurse(nurse);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
        return "redirect:/" + role.name().toLowerCase() + "/profile";
    }
}
