package com.medical.schoolMedical.modules.healthcheck.controllers;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckConsentDTO;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckConsentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/parent/confirm/confirm-health-check")
public class ConfirmHealthCheck {
    @Autowired
    private HealthCheckConsentService healthCheckConsentService;

    @GetMapping
    public String confirmHealthCheck(@RequestParam("idConsent") Long consentId, Model model, RedirectAttributes redirectAttributes) {
        try {
            HealthCheckConsentDTO healthCheck = healthCheckConsentService.getHealthCheckConsentById(consentId);
            log.info("healthCheck in confirmHealthCheck: {}", healthCheck);
            model.addAttribute("healthCheckConsent", healthCheck);
            return "parent/confirmHealthCheckConsent";
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/parent/notification/HealthCheckConsents";
        }
    }

    @PostMapping
    public String confirmHealthCheck(@RequestParam("consentId") Long id,
                                     @RequestParam("response") String response,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        try {
            healthCheckConsentService.updateHealthCheckConsent(id, response);
            redirectAttributes.addFlashAttribute("success", "Xác nhận khám sức khỏe thành công");
            return "redirect:/parent/notification/HealthCheckConsents";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "parent/confirmHealthCheckConsent";
        }
    }
}
