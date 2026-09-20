package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.auth.services.NotificationService;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckConsentService;
import com.medical.schoolMedical.modules.user_management.dto.ParentDTO;
import com.medical.schoolMedical.modules.user_management.services.ParentService;
import com.medical.schoolMedical.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/parent")
public class ParentController {
    HealthCheckConsentService healthCheckConsentService;
    NotificationService notificationService;
    ParentService parentService;

    @ModelAttribute("notifications")
    public Map<String, Boolean> getNotifications(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUser().getId();
        try{
            ParentDTO parentDTO = parentService.getParentbyId(userId);
            Long parentId = parentDTO.getId();
            return notificationService.getUserNotifications(parentId);

        }catch(BusinessException e){
            log.warn("Không thể lấy thông báo cho phụ huynh (userId={}): {}", userId, e.getMessage());
            return Map.of(
                    "newConsent", false,
                    "newRecord", false
            );
        }

    }

    @GetMapping("/parent-home")
    public String parentHome(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails,
                             HttpSession session) {
        String username = customUserDetails.getUser().getUsername(); // Lấy tên người dùng đang đăng nhập
        model.addAttribute("username", username);

        return "parent/parent-home"; // Trả về view: templates/parent/home.html
    }

}
