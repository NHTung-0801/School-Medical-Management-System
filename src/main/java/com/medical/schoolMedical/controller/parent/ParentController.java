package com.medical.schoolMedical.controller.parent;

import com.medical.schoolMedical.dto.ParentDTO;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckConsentService;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.service.NotificationService;
import com.medical.schoolMedical.service.ParentService;
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


    //    Nếu hiện thông báo rồi thì đánh dấu nó để k hiện lại nhiều lần
//    @PostMapping("/notification/dismiss")
//    @ResponseBody
//    public void dismissNotification(HttpSession session) {
//        session.setAttribute("notificationSeen", true);
//    }


    @GetMapping("/parent-home")
    public String parentHome(Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails,
                             HttpSession session) {
        String username = customUserDetails.getUser().getUsername(); // Lấy tên người dùng đang đăng nhập
        model.addAttribute("username", username);
//        Long loggedInUserID = customUserDetails.getUser().getId();
//        log.info("loggedInUserID : {}", loggedInUserID);
//
////        Kiểm tra xem có phiếu kiểm tra sức khỏe nào mới không
//        boolean hasNewHealthCheckNotification = healthCheckConsentService.hasNewNotification(loggedInUserID);
//        log.info("hasNewHealthCheckNotification : {}", hasNewHealthCheckNotification);
//
////        Kiểm tra nếu đã hiện thông báo rồi thì không hiện trong phiên đó nx
//        Boolean notificationSeen = (Boolean) session.getAttribute("notificationSeen");
//        // Nếu chưa từng thấy trong session này
//        boolean notSeenYet  = notificationSeen == null || !notificationSeen;
////Chỉ hiện nếu có phiếu mới và Chưa xem trong phiên
//        boolean showNotification = hasNewHealthCheckNotification && notSeenYet;
//
//        model.addAttribute("showNotification", showNotification);

        return "parent/parent-home"; // Trả về view: templates/parent/home.html
    }

}
