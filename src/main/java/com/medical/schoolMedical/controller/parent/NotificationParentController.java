package com.medical.schoolMedical.controller.parent;

import com.medical.schoolMedical.dto.*;
import com.medical.schoolMedical.modules.healthcheck.dto.*;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckConsentService;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckRecordService;
import com.medical.schoolMedical.modules.consultation.dto.ConsultationAppointmentDTO;
import com.medical.schoolMedical.modules.consultation.services.ConsultationAppointmentService;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationRecordDTO;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationRecord;
import com.medical.schoolMedical.modules.vaccination.services.VaccinationConsentService;
import com.medical.schoolMedical.modules.vaccination.services.VaccinationRecordService;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequestMapping("/parent/notification")
public class NotificationParentController {
    @Autowired
    private HealthCheckConsentService healthCheckConsentService;
    @Autowired
    private HealthCheckRecordService healthCheckRecordService;
    @Autowired
    private VaccinationConsentService vaccinationConsentService;
    @Autowired
    private VaccinationRecordService vaccinationRecordService;
    @Autowired
    private ConsultationAppointmentService consultationAppointmentService;

// health check
    @GetMapping("/HealthCheckConsents")
    public String listHealthCheckConsent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {
        Long userId = customUserDetails.getUser().getId();
//        log.info("userId: {}", userId);

        Page<HealthCheckConsentDTO> listConsent = healthCheckConsentService.getHealthCheckConsentByParentId(userId, page);

        log.info("listHealthCheckConsent in consent Parent: {}", listConsent.getContent());

        model.addAttribute("listHealthCheckConsent", listConsent.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listConsent.getTotalPages());
        return "parent/listHealthCheckConsent";

    }

    @GetMapping("/HealthCheckRecords")
    public String listHealthCheckRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {
        Long userId = customUserDetails.getUser().getId();
//        log.info("userId: {}", userId);

        Page<HealthCheckRecordDTO> listRecord = healthCheckRecordService.getSentRecordsToParents(userId, page);

        model.addAttribute("listHealthCheckRecord", listRecord.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listRecord.getTotalPages());
        return "parent/listHealthCheckRecord";

    }

//    vaccination
    @GetMapping("/vaccinationConsents")
    public String listVaccinationConsent(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @RequestParam(defaultValue = "0") int page,
                                         Model model) {
        Long userId = customUserDetails.getUser().getId();
//        log.info("userId: {}", userId);

        Page<VaccinationConsentDTO> listConsent = vaccinationConsentService.getVaccinationConsentByParentId(userId, page);

        model.addAttribute("listVaccinationConsent", listConsent.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listConsent.getTotalPages());
        return "parent/listVaccinationConsent";

    }

    @GetMapping("/VaccinationRecords")
    public String listVaccinationRecord(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestParam(defaultValue = "0") int page,
                                        Model model) {
        Long userId = customUserDetails.getUser().getId();
//        log.info("userId: {}", userId);

        Page<VaccinationRecordDTO> listRecord = vaccinationRecordService.getSentRecordsToParents(userId, page);

        model.addAttribute("listVaccinationRecord", listRecord.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", listRecord.getTotalPages());
        return "parent/listVaccinationRecord";

    }

    @GetMapping("/ConsultationAppointments")
    public String listConsultationAppointment(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                              @RequestParam(defaultValue = "0") int page,
                                              Model model) {
        Long userId = customUserDetails.getUser().getId();

        Page<ConsultationAppointmentDTO> appointmentDTOS = consultationAppointmentService.getAllAppointment_parent(userId, page);

        model.addAttribute("appointmentDTOS", appointmentDTOS.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", appointmentDTOS.getTotalPages());
        return "parent/ListAppointmentNotification";

    }

}
