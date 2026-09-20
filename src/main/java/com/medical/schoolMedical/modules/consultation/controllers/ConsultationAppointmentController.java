package com.medical.schoolMedical.modules.consultation.controllers;

import com.medical.schoolMedical.modules.user_management.dto.StudentDTO;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.consultation.dto.ConsultationAppointmentDTO;
import com.medical.schoolMedical.modules.consultation.entities.ConsultationAppointment;
import com.medical.schoolMedical.modules.consultation.services.ConsultationAppointmentService;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import com.medical.schoolMedical.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/nurse/consultationAppointment")
public class ConsultationAppointmentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ConsultationAppointmentService consultationAppointmentService;

    @GetMapping("/consultation-appointment")
    public String createReview(Model model) {
        List<StudentDTO> studentDTOS = studentService.getAllStudentsDTO();
        log.info("studentDTOS = {}", studentDTOS);
        model.addAttribute("students", studentDTOS);
        model.addAttribute("consultationAppointment", new ConsultationAppointmentDTO());
        return "nurse/createReview";
    }

    @PostMapping("/save-consultationAppointment")
    public String saveConsultationAppointment(@ModelAttribute("consultationAppointment") @Valid ConsultationAppointmentDTO consultationAppointmentDTO,
                                              BindingResult bindingResult,
                                              Model model,
                                              RedirectAttributes redirectAttributes,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (bindingResult.hasErrors()) {
            List<StudentDTO> studentDTOS = studentService.getAllStudentsDTO();
            model.addAttribute("students", studentDTOS);
            return "nurse/createReview";
        }

        // Lấy userID để biết y tá nào tạo lịch hẹn
        long userId = customUserDetails.getUser().getId();

        try {
            // Vừa tạo lịch hẹn, vừa gửi đến phụ huynh
            consultationAppointmentService.createConsAppoint(consultationAppointmentDTO, userId);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/nurse-home";
        }

        redirectAttributes.addFlashAttribute("success", "Lịch tư vấn đã được tạo thành công!");
        return "redirect:/nurse/nurse-home";
    }

    // Danh sách lịch tư vấn (đã xác nhận và sắp đến ngày tư vấn)
    @GetMapping("/listReview")
    public String listReview(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<ConsultationAppointmentDTO> appointmentDTOS = consultationAppointmentService.getAllAppointmentAccepted(page);
        model.addAttribute("appointmentDTOS", appointmentDTOS.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentDTOS.getTotalPages());
        return "nurse/listReview";
    }

    // Danh sách các lịch tư vấn đã tạo
    @GetMapping("/listCreatedReview")
    public String listCreatedReview(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<ConsultationAppointmentDTO> appointmentDTOS = consultationAppointmentService.getAllConsultationAppointments(page);
        model.addAttribute("appointmentDTOS", appointmentDTOS.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentDTOS.getTotalPages());
        return "nurse/listCreatedReview";
    }

    // Hiện form tạo lịch hẹn cho học sinh được chọn từ: có kq khám nhưng cần tư vấn
    @GetMapping("/healthCheckRecord/createAppointment")
    public String createAppointment_healthCheckRecord(Model model,
                                                      @RequestParam(value = "idStudent", required = false) Long studentId,
                                                      @RequestParam(value = "idSchedule", required = false) Long idSchedule,
                                                      RedirectAttributes redirectAttributes) {
        if (studentId == null || idSchedule == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn học sinh và lịch khám sức khỏe phù hợp để tạo lịch hẹn tư vấn.");
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health/needsConsultation?idSchedule=" + idSchedule;
        }

        StudentDTO studentDTOS;
        try {
            studentDTOS = studentService.getStudentById_DTO(studentId);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health/needsConsultation?idSchedule=" + idSchedule;
        }

        ConsultationAppointmentDTO dto = new ConsultationAppointmentDTO();
        // Gán sẵn id để bên form nó chọn học sinh được chọn làm mặc định luôn
        dto.setStudentId(studentId);

        model.addAttribute("students", studentDTOS);
        model.addAttribute("idSchedule", idSchedule);
        model.addAttribute("consultationAppointment", dto);
        return "nurse/createReview";
    }
}
