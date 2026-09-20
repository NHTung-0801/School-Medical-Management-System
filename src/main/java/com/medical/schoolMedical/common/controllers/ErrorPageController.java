package com.medical.schoolMedical.common.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorPageController {
    @GetMapping("/error/general")
    public String showGeneralErrorPage() {
        return "common/loichung";
    }
}
