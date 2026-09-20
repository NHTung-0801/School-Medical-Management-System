package com.medical.schoolMedical.modules.vaccination.controllers;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationRecordDTO;
import com.medical.schoolMedical.modules.vaccination.services.VaccinationConsentService;
import com.medical.schoolMedical.modules.vaccination.services.VaccinationRecordService;
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
@RequestMapping("/nurse/vaccinationRecord")
public class VaccinationRecordController {
    @Autowired
    VaccinationRecordService vaccinationRecordService;
    @Autowired
    VaccinationConsentService vaccinationConsentService;

    @GetMapping("/form")
    public String vaccinationRecord(@RequestParam(value = "vaccinatonScheduleId", required = false) Long idSchedule,
                                    @RequestParam(value = "vaccinatonConsentId", required = false) Long consentId,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (idSchedule == null || consentId == null) {
            redirectAttributes.addFlashAttribute("error", "id học sinh hoặc id đồng ý tiêm chủng không được để trống.");
            return "redirect:/nurse/vaccinationSchedule/vaccinationSchedules";
        }

        VaccinationRecordDTO vaccinationRecordDTO = new VaccinationRecordDTO();
        model.addAttribute("vaccinationRecordDTO", vaccinationRecordDTO);
        model.addAttribute("vaccinatonConsentId", consentId);
        model.addAttribute("vaccinatonScheduleId", idSchedule);
        return "nurse/vaccinationRecordNurse";
    }

    // Hàm kiểm tra xem đang tạo record hay update và thực hiện gọi hàm tương ứng
    @PostMapping("/save-update-vaccinationRecord")
    public String controllerAction_vaccinationRecord(@ModelAttribute("vaccinationRecordDTO") @Valid VaccinationRecordDTO vaccinationRecordDTO,
                                                     BindingResult bindingResult,
                                                     @RequestParam(value = "vaccinatonConsentId") Long consentId,
                                                     @RequestParam(value = "vaccinatonScheduleId") Long idSchedule,
                                                     @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     RedirectAttributes redirectAttributes,
                                                     Model model) {
        log.info("consentId in controllerAction_vaccinationRecord: {}", consentId);
        if (bindingResult.hasErrors()) {
            model.addAttribute("vaccinatonConsentId", consentId);
            model.addAttribute("vaccinatonScheduleId", idSchedule);
            return "nurse/vaccinationRecordNurse";
        }

        log.info("vaccinationRecordDTO của /save-update-vaccinationRecord: {}", vaccinationRecordDTO);

        if (vaccinationRecordDTO.getId() == 0) {
            return saveVaccinationRecord(vaccinationRecordDTO,
                    consentId,
                    idSchedule,
                    customUserDetails,
                    redirectAttributes,
                    model);
        } else {
            log.info("chạy cái update");
            return updateVaccinationRecord(vaccinationRecordDTO,
                    consentId,
                    idSchedule,
                    customUserDetails,
                    redirectAttributes,
                    model);
        }
    }

    public String saveVaccinationRecord(VaccinationRecordDTO vaccinationRecordDTO,
                                        Long consentId,
                                        Long idSchedule,
                                        CustomUserDetails customUserDetails,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        // Lấy id của người tạo form
        Long userId = customUserDetails.getUser().getId();
        log.info("userId ==> " + userId);

        // Tạo record
        try {
            vaccinationRecordService.create_VaccinationRecord(vaccinationRecordDTO, consentId, userId);
            redirectAttributes.addFlashAttribute("success", "Kết quả sau tiêm chủng đã được ghi nhận thành công!");
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination?vaccinatonScheduleId=" + idSchedule;
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination?vaccinatonScheduleId=" + idSchedule;
        }
    }

    // Cập nhật record
    @GetMapping("/updateForm")
    public String Update_healthCheckRecord(@RequestParam(value = "vaccinatonScheduleId", required = false) Long idSchedule,
                                           @RequestParam(value = "vaccinatonConsentId", required = false) Long consentId,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {
        if (idSchedule == null || consentId == null) {
            redirectAttributes.addFlashAttribute("error", "id học sinh hoặc id đồng ý tiêm chủng không được để trống.");
            return "redirect:/nurse/vaccinationSchedule/vaccinationSchedules";
        }

        try {
            VaccinationConsentDTO vaccinationConsentDTO = vaccinationConsentService.getVaccinationConsentDTO_ById(consentId);
            log.info("healthCheckConsentDTO của updateForm ==> " + vaccinationConsentDTO.toString());

            // Tìm xem record đã có chưa thì mới cho update
            VaccinationRecordDTO vaccinationRecordDTO;
            try {
                vaccinationRecordDTO = vaccinationRecordService.toVaccinationRecordDTO(vaccinationConsentDTO);
                vaccinationRecordDTO.setVaccinationConsentId(vaccinationRecordDTO.getVaccinationConsent().getId());
                log.info("vaccinationRecordDTO của updateForm ==> " + vaccinationRecordDTO.toString());
            } catch (BusinessException e) {
                redirectAttributes.addFlashAttribute("notification", e.getMessage());
                return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
            }
            // Nếu có thì hiện form với các thông tin có sẵn
            model.addAttribute("vaccinationRecordDTO", vaccinationRecordDTO);
            model.addAttribute("vaccinatonConsentId", consentId);
            model.addAttribute("vaccinatonScheduleId", idSchedule);
            return "nurse/vaccinationRecordNurse";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        }
    }

    // Chỉnh sửa thông tin record:
    public String updateVaccinationRecord(VaccinationRecordDTO vaccinationRecordDTO_request,
                                          Long consentId,
                                          Long idSchedule,
                                          CustomUserDetails customUserDetails,
                                          RedirectAttributes redirectAttributes,
                                          Model model) {
        log.info("data của vaccinationRecordDTO_request khi gửi lên để update: {}", vaccinationRecordDTO_request.toString());
        // Lấy vaccinationConsentDTO phù hợp với id được gửi tới
        VaccinationConsentDTO vaccinationConsentDTO;
        try {
            vaccinationConsentDTO = vaccinationConsentService.getVaccinationConsentDTOById(consentId);
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        }

        // Gán obj vaccinationConsentDTO vừa lấy vô vaccinationRecordDTO
        vaccinationRecordDTO_request.setVaccinationConsent(vaccinationConsentDTO);

        // Lấy id của user tương ứng
        Long userId = customUserDetails.getUser().getId();
        log.info("nurseId ==> " + userId);
        try {
            vaccinationRecordService.update_VaccinationRecord(vaccinationRecordDTO_request, userId);
            redirectAttributes.addFlashAttribute("success", "Cập nhật kết quả tiêm thành công!");
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        }
    }

    // Gửi vaccination record đến phụ huynh
    @PostMapping("/sendRecordsToParents")
    public String sendRecordsToParents(@RequestParam(value = "selectedRecordIds", required = false) List<Long> selectedIds,
                                       @RequestParam("idSchedule") Long idSchedule,
                                       RedirectAttributes redirectAttributes) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn ít nhất một bản ghi để gửi!");
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        }

        try {
            vaccinationRecordService.sendRecordsToParents(selectedIds);
            redirectAttributes.addFlashAttribute("success", "Gửi kết quả tiêm chủng đến phụ huynh thành công!");
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/vaccinationConsent/list-student-vaccination/vaccinated?vaccinatonScheduleId=" + idSchedule;
        }
    }
}
