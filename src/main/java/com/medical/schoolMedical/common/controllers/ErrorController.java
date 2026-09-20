package com.medical.schoolMedical.common.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    // không đủ quyền truy cập sẽ chuyển trang
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
