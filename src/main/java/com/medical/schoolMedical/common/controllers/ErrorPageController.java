package com.medical.schoolMedical.common.controllers;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {

    @GetMapping("/error/general")
    public String showGeneralErrorPage(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        String homeUrl = "/";
        String homeTitle = "Trang Chủ";

        if (customUserDetails != null && customUserDetails.getUser() != null) {
            Role role = customUserDetails.getUser().getRole();
            if (role != null) {
                switch (role) {
                    case ADMIN -> {
                        homeUrl = "/admin/dashboard";
                        homeTitle = "Bảng Quản Trị Hệ Thống";
                    }
                    case NURSE -> {
                        homeUrl = "/nurse/nurse-home";
                        homeTitle = "Cổng Y Tế Học Đường";
                    }
                    case PARENT -> {
                        homeUrl = "/parent/parent-home";
                        homeTitle = "Cổng Phụ Huynh";
                    }
                    case MANAGER -> {
                        homeUrl = "/manager/manager-home";
                        homeTitle = "Cổng Quản Lý";
                    }
                }
            }
        }

        model.addAttribute("homeUrl", homeUrl);
        model.addAttribute("homeTitle", homeTitle);
        return "common/loichung";
    }
}

