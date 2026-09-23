package com.medical.schoolMedical.modules.auth.services;

import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckConsentRepository;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckRecordRepository;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationConsentRepository;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private HealthCheckConsentRepository healthCheckConsentRepository;

    @Mock
    private HealthCheckRecordRepository healthCheckRecordRepository;

    @Mock
    private VaccinationConsentRepository vaccinationConsentRepository;

    @Mock
    private VaccinationRecordRepository vaccinationRecordRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should return correct notifications and count when parent has unread items across all modules")
    void getUserNotifications_whenHasAllNotifications_shouldReturnCorrectFlagsAndCount() {
        Long parentId = 100L;

        when(healthCheckConsentRepository.existsByParent_IdAndStatus(eq(parentId), eq(ConsentStatus.UNCONFIRMED))).thenReturn(true);
        when(healthCheckRecordRepository.existsByHealthCheckConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(eq(parentId))).thenReturn(true);
        when(vaccinationConsentRepository.existsByParent_IdAndStatus(eq(parentId), eq(ConsentStatus.UNCONFIRMED))).thenReturn(true);
        when(vaccinationRecordRepository.existsByVaccinationConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(eq(parentId))).thenReturn(true);

        Map<String, Object> result = notificationService.getUserNotifications(parentId);

        assertNotNull(result);
        assertEquals(true, result.get("hasNewHealthCheckConsent"));
        assertEquals(true, result.get("hasNewHealthCheckRecord"));
        assertEquals(true, result.get("hasNewVaccinationConsent"));
        assertEquals(true, result.get("hasNewVaccinationRecord"));
        assertEquals(true, result.get("hasNewConsent"));
        assertEquals(true, result.get("hasNewRecord"));
        assertEquals(true, result.get("hasAnyNotification"));
        assertEquals(4, result.get("unreadCount"));
    }

    @Test
    @DisplayName("Should return zero unread and false flags when parent has no pending notifications")
    void getUserNotifications_whenNoNotifications_shouldReturnEmptyFlagsAndZeroCount() {
        Long parentId = 100L;

        when(healthCheckConsentRepository.existsByParent_IdAndStatus(eq(parentId), eq(ConsentStatus.UNCONFIRMED))).thenReturn(false);
        when(healthCheckRecordRepository.existsByHealthCheckConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(eq(parentId))).thenReturn(false);
        when(vaccinationConsentRepository.existsByParent_IdAndStatus(eq(parentId), eq(ConsentStatus.UNCONFIRMED))).thenReturn(false);
        when(vaccinationRecordRepository.existsByVaccinationConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(eq(parentId))).thenReturn(false);

        Map<String, Object> result = notificationService.getUserNotifications(parentId);

        assertNotNull(result);
        assertEquals(false, result.get("hasNewHealthCheckConsent"));
        assertEquals(false, result.get("hasNewHealthCheckRecord"));
        assertEquals(false, result.get("hasNewVaccinationConsent"));
        assertEquals(false, result.get("hasNewVaccinationRecord"));
        assertEquals(false, result.get("hasNewConsent"));
        assertEquals(false, result.get("hasNewRecord"));
        assertEquals(false, result.get("hasAnyNotification"));
        assertEquals(0, result.get("unreadCount"));
    }

    @Test
    @DisplayName("Should return partial notifications when only vaccination items are pending")
    void getUserNotifications_whenPartialNotifications_shouldReturnPartialFlags() {
        Long parentId = 100L;

        when(healthCheckConsentRepository.existsByParent_IdAndStatus(eq(parentId), eq(ConsentStatus.UNCONFIRMED))).thenReturn(false);
        when(healthCheckRecordRepository.existsByHealthCheckConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(eq(parentId))).thenReturn(false);
        when(vaccinationConsentRepository.existsByParent_IdAndStatus(eq(parentId), eq(ConsentStatus.UNCONFIRMED))).thenReturn(true);
        when(vaccinationRecordRepository.existsByVaccinationConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(eq(parentId))).thenReturn(false);

        Map<String, Object> result = notificationService.getUserNotifications(parentId);

        assertNotNull(result);
        assertEquals(false, result.get("hasNewHealthCheckConsent"));
        assertEquals(false, result.get("hasNewHealthCheckRecord"));
        assertEquals(true, result.get("hasNewVaccinationConsent"));
        assertEquals(false, result.get("hasNewVaccinationRecord"));
        assertEquals(true, result.get("hasNewConsent"));
        assertEquals(false, result.get("hasNewRecord"));
        assertEquals(true, result.get("hasAnyNotification"));
        assertEquals(1, result.get("unreadCount"));
    }
}
