package com.medical.schoolMedical.service;

import com.medical.schoolMedical.entities.ConsultationAppointment;
import com.medical.schoolMedical.modules.healthcheck.services.HealthCheckConsentService;
import com.medical.schoolMedical.modules.vaccination.services.VaccinationConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

@Component
public class ConsentStatusSchedulerService {
    private final VaccinationConsentService vaccinationConsentService;
    private final HealthCheckConsentService healthCheckConsentService;
    private final ConsultationAppointmentService consultationAppointmentService;

    @Autowired
    public ConsentStatusSchedulerService(VaccinationConsentService vaccinationConsentService
            , HealthCheckConsentService healthCheckConsentService
            ,ConsultationAppointmentService consultationAppointmentService) {
        this.vaccinationConsentService = vaccinationConsentService;
        this.healthCheckConsentService = healthCheckConsentService;
        this.consultationAppointmentService = consultationAppointmentService;
    }

    // Cron job chạy lúc 7h mỗi ngày
    @Scheduled(cron = "0 0 7 * * *")
    public void scheduleCheck() {
        vaccinationConsentService.update_SurveyExpired_VaccinationConsent();
        healthCheckConsentService.update_SurveyExpired_HealthCheckConsent();
        consultationAppointmentService.update_SurveyExpired_Appointment();

    }

    // Chạy ngay sau khi ứng dụng khởi động
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        vaccinationConsentService.update_SurveyExpired_VaccinationConsent();
        healthCheckConsentService.update_SurveyExpired_HealthCheckConsent();
        consultationAppointmentService.update_SurveyExpired_Appointment();
    }
}

