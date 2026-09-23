package com.medical.schoolMedical.modules.auth.services;

import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckConsentRepository;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckRecordRepository;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationConsentRepository;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationRecordRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    HealthCheckConsentRepository healthCheckConsentRepository;
    HealthCheckRecordRepository healthCheckRecordRepository;
    VaccinationConsentRepository vaccinationConsentRepository;
    VaccinationRecordRepository vaccinationRecordRepository;

    public Map<String, Object> getUserNotifications(Long parentId) {
        Map<String, Object> notifications = new HashMap<>();

        boolean hasNewHealthCheckConsent = healthCheckConsentRepository.existsByParent_IdAndStatus(parentId, ConsentStatus.UNCONFIRMED);
        boolean hasNewHealthCheckRecord = healthCheckRecordRepository.existsByHealthCheckConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(parentId);
        boolean hasNewVaccinationConsent = vaccinationConsentRepository.existsByParent_IdAndStatus(parentId, ConsentStatus.UNCONFIRMED);
        boolean hasNewVaccinationRecord = vaccinationRecordRepository.existsByVaccinationConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(parentId);

        int unreadCount = 0;
        if (hasNewHealthCheckConsent) unreadCount++;
        if (hasNewHealthCheckRecord) unreadCount++;
        if (hasNewVaccinationConsent) unreadCount++;
        if (hasNewVaccinationRecord) unreadCount++;

        boolean hasAny = unreadCount > 0;

        notifications.put("hasNewHealthCheckConsent", hasNewHealthCheckConsent);
        notifications.put("hasNewHealthCheckRecord", hasNewHealthCheckRecord);
        notifications.put("hasNewVaccinationConsent", hasNewVaccinationConsent);
        notifications.put("hasNewVaccinationRecord", hasNewVaccinationRecord);

        // Giữ tương thích ngược với code cũ
        notifications.put("hasNewConsent", hasNewHealthCheckConsent || hasNewVaccinationConsent);
        notifications.put("hasNewRecord", hasNewHealthCheckRecord || hasNewVaccinationRecord);
        notifications.put("hasAnyNotification", hasAny);
        notifications.put("unreadCount", unreadCount);

        return notifications;
    }
}

