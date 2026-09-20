package com.medical.schoolMedical.modules.healthcheck.controllers;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckConsentDTO;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckRecordDTO;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckConsentService;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckRecordService;
import com.medical.schoolMedical.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/nurse/healthCheckRecord")
public class HealthCheckRecordController {
    @Autowired
    HealthCheckConsentService healthCheckConsentService;
    @Autowired
    HealthCheckRecordService healthCheckRecordService;

    @GetMapping("/form")
    public String healthCheckRecord(@RequestParam(value = "idSchedule", required = false) Long idSchedule,
                                    @RequestParam(value = "idConsent", required = false) Long consentId,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (idSchedule == null || consentId == null) {
            redirectAttributes.addFlashAttribute("error", "id học sinh hoặc id đồng ý khám sức khỏe không được để trống.");
            return "redirect:/nurse/healthCheckSchedule/list-healthCheckSchedule";
        }

        HealthCheckRecordDTO healthCheckRecordDTO = new HealthCheckRecordDTO();
        model.addAttribute("healthCheckRecordDTO", healthCheckRecordDTO);
        model.addAttribute("consentId", consentId);
        model.addAttribute("idSchedule", idSchedule);
        return "admin/healthCheckRecord";
    }

    // Hàm kiểm tra xem đang tạo record hay update và thực hiện gọi hàm tương ứng
    @PostMapping("/save-update-Form")
    public String controllerAction(@ModelAttribute("healthCheckRecordDTO") @Valid HealthCheckRecordDTO healthCheckRecordDTO,
                                   BindingResult bindingResult,
                                   @RequestParam(value = "consentId") Long consentId,
                                   @RequestParam(value = "idSchedule") Long idSchedule,
                                   @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        log.info("consentId in controllerAction: {}", consentId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("consentId", consentId);
            model.addAttribute("idSchedule", idSchedule);
            return "admin/healthCheckRecord";
        }

        log.info("healthCheckRecordDTO của /save-update-Form: {}", healthCheckRecordDTO);
        if (healthCheckRecordDTO.getId() == 0) {
            return saveHealthCheckRecord(healthCheckRecordDTO,
                    consentId,
                    idSchedule,
                    customUserDetails,
                    redirectAttributes,
                    model);
        } else {
            return updateHealthCheckRecord(healthCheckRecordDTO,
                    consentId,
                    idSchedule,
                    customUserDetails,
                    redirectAttributes,
                    model);
        }
    }

    public String saveHealthCheckRecord(HealthCheckRecordDTO healthCheckRecordDTO,
                                        Long consentId,
                                        Long idSchedule,
                                        CustomUserDetails customUserDetails,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        Long userId = customUserDetails.getUser().getId();
        log.info("userId ==> " + userId);

        try {
            healthCheckRecordService.create_HealthCheckRecord(healthCheckRecordDTO, consentId, userId);
            redirectAttributes.addFlashAttribute("success", "Kết quả khám đã được ghi nhận thành công!");
            return "redirect:/nurse/healthCheckConsent/list-student-health-check?idSchedule=" + idSchedule;
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check?idSchedule=" + idSchedule;
        }
    }

    // Cập nhật record
    @GetMapping("/updateForm")
    public String Update_healthCheckRecord(@RequestParam(value = "idSchedule", required = false) Long idSchedule,
                                           @RequestParam(value = "idConsent", required = false) Long consentId,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {
        if (idSchedule == null || consentId == null) {
            redirectAttributes.addFlashAttribute("error", "id học sinh hoặc id đồng ý khám sức khỏe không được để trống.");
            return "redirect:/nurse/healthCheckSchedule/list-healthCheckSchedule";
        }

        try {
            HealthCheckConsentDTO healthCheckConsentDTO = healthCheckConsentService.getHealthCheckConsentDTO_ById(consentId);
            log.info("healthCheckConsentDTO của updateForm ==> " + healthCheckConsentDTO.toString());

            HealthCheckRecordDTO healthCheckRecordDTO;
            try {
                healthCheckRecordDTO = healthCheckRecordService.toHealthCheckRecordDTO(healthCheckConsentDTO);
                healthCheckRecordDTO.setHealthCheckConsentId(healthCheckRecordDTO.getHealthCheckConsentDTO().getId());
            } catch (BusinessException e) {
                redirectAttributes.addFlashAttribute("notification", e.getMessage());
                return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
            }
            model.addAttribute("healthCheckRecordDTO", healthCheckRecordDTO);
            model.addAttribute("consentId", consentId);
            model.addAttribute("idSchedule", idSchedule);
            return "admin/healthCheckRecord";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }
    }

    // Chỉnh sửa thông tin record:
    public String updateHealthCheckRecord(HealthCheckRecordDTO healthCheckRecordDTO_request,
                                          Long consentId,
                                          Long idSchedule,
                                          CustomUserDetails customUserDetails,
                                          RedirectAttributes redirectAttributes,
                                          Model model) {
        log.info("data của healthCheckRecordDTO khi gửi lên để update: {}", healthCheckRecordDTO_request.toString());
        HealthCheckConsentDTO healthCheckConsentDTO;
        try {
            healthCheckConsentDTO = healthCheckConsentService.getHealthCheckConsentById(consentId);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }

        healthCheckRecordDTO_request.setHealthCheckConsentDTO(healthCheckConsentDTO);

        Long nurseId = customUserDetails.getUser().getId();
        log.info("nurseId ==> " + nurseId);
        try {
            healthCheckRecordService.update_HealthCheckRecord(healthCheckRecordDTO_request, nurseId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật kết quả khám thành công!");
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }
    }

    // Gửi health check record đến phụ huynh
    @PostMapping("/sendRecordsToParents")
    public String sendRecordsToParents(@RequestParam(value = "selectedRecordIds", required = false) List<Long> selectedIds,
                                       @RequestParam("idSchedule") Long idSchedule,
                                       RedirectAttributes redirectAttributes) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất một bản ghi để gửi!");
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }

        try {
            healthCheckRecordService.sendRecordsToParents(selectedIds);
            redirectAttributes.addFlashAttribute("success", "Gửi kết quả khám sức khỏe đến phụ huynh thành công!");
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }
    }

    // Nurse xem kq khám sức khỏe của học sinh đã khám
    @GetMapping("/viewHealthCheckRecord")
    public String viewHealthCheckRecord(Model model,
                                        RedirectAttributes redirectAttributes,
                                        @RequestParam(value = "idConsent", required = false) Long idConsent,
                                        @RequestParam(value = "idSchedule", required = false) Long idSchedule) {
        if (idSchedule == null || idConsent == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn bản ghi khám sức khỏe phù hợp để xem chi tiết.");
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }

        try {
            HealthCheckRecordDTO healthCheckRecordDTO = healthCheckRecordService.getCheckRecord(idConsent);
            model.addAttribute("healthCheckRecord", healthCheckRecordDTO);
            model.addAttribute("idSchedule", idSchedule);
            log.info("healthCheckRecord in viewHealthCheckRecord: {}", healthCheckRecordDTO);
            return "nurse/XemKQDaKhamSucKhoe";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/healthCheckConsent/list-student-health-check/checked-health?idSchedule=" + idSchedule;
        }
    }
}
