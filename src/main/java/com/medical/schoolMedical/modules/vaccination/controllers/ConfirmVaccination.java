package com.medical.schoolMedical.modules.vaccination.controllers;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.modules.vaccination.services.VaccinationConsentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/parent/confirm/confirm-vaccination")
public class ConfirmVaccination {
    @Autowired
    private VaccinationConsentService vaccinationConsentService;

    @GetMapping
    public String confirmVaccination(@RequestParam(value = "vaccinatonConsentId", required = false) Long consentId,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        if (consentId == null) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn lịch tiêm chủng phù hợp để xác nhận");
            return "redirect:/nurse/nurse-home";
        }
        try {
            VaccinationConsentDTO vaccinationConsentDTO = vaccinationConsentService.getVaccinationConsentDTOById(consentId);
            log.info("vaccinationConsentDTO in confirmVaccination: {}", vaccinationConsentDTO);
            model.addAttribute("vaccinationConsent", vaccinationConsentDTO);
            return "parent/confirmVaccinationConsent";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/nurse/nurse-home";
        }
    }

    @PostMapping
    public String confirmVaccination(@RequestParam("vaccinatonConsentId") Long id,
                                     @RequestParam("response") String response,
                                     Model model, RedirectAttributes redirectAttributes) {
        try {
            vaccinationConsentService.updateVaccinationConsent(id, response);
            redirectAttributes.addFlashAttribute("success", "Xác nhận tiêm chủng thành công");
            return "redirect:/parent/notification/vaccinationConsents";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "parent/confirmVaccinationConsent";
        }
    }
}
