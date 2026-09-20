package com.medical.schoolMedical.modules.consultation.controllers;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.consultation.dto.ConsultationAppointmentDTO;
import com.medical.schoolMedical.modules.consultation.services.ConsultationAppointmentService;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/parent/consultationAppointment")
public class ConsultationAppointmentParentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ConsultationAppointmentService consultationAppointmentService;

    @GetMapping("/listReview")
    public String listParentReview(Model model, @RequestParam(defaultValue = "0") int page,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // Lấy id user của phụ huynh
        long userId = customUserDetails.getUser().getId();
        Page<ConsultationAppointmentDTO> appointmentDTOS = consultationAppointmentService.getAllAppointmentAccepted_Parent(userId, page);
        model.addAttribute("appointmentDTOS", appointmentDTOS.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentDTOS.getTotalPages());
        return "parent/listReview";
    }

    @GetMapping("/confirmReview")
    public String confirmReview(@RequestParam(value = "appointmentId", required = false) Long appointmentId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (appointmentId == null) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn lịch tư vấn phù hợp để xác nhận");
            return "redirect:/parent/parent-home";
        }
        ConsultationAppointmentDTO consultationAppointmentDTO;
        try {
            consultationAppointmentDTO = consultationAppointmentService.getAndUpdateViewedByParent_ConsultationAppointment(appointmentId);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parent/notification/ConsultationAppointments";
        }

        model.addAttribute("consultationAppointmentDTO", consultationAppointmentDTO);
        return "parent/confirmReview";
    }

    // Confirm lịch hẹn
    @PostMapping("/confirmReview")
    public String confirmAppointment(@RequestParam("appointmentId") Long appointmentId,
                                     @RequestParam("response") String response,
                                     RedirectAttributes redirectAttributes) {
        try {
            consultationAppointmentService.confirmAppointment(appointmentId, response);
            redirectAttributes.addFlashAttribute("success", "Xác nhận lịch tư vấn sức khỏe thành công");
            return "redirect:/parent/notification/ConsultationAppointments";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parent/notification/ConsultationAppointments";
        }
    }
}
