package com.medical.schoolMedical.service;

import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckConsentRepository;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckRecordRepository;
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

    public Map<String, Boolean> getUserNotifications(Long parentId) {
        Map<String, Boolean> notifications = new HashMap<>();

        boolean hasNewConsent = healthCheckConsentRepository.existsByParent_IdAndStatus(parentId, ConsentStatus.UNCONFIRMED);
        boolean hasNewRecord = healthCheckRecordRepository.existsByHealthCheckConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(parentId);
//        log.info("hasNewConsent : {} and hasNewRecord: {}", hasNewConsent, hasNewRecord);

        notifications.put("hasNewConsent", hasNewConsent);
        notifications.put("hasNewRecord", hasNewRecord);
//        log.info("Notifications map: {}", notifications);
        return notifications;
    }
}
