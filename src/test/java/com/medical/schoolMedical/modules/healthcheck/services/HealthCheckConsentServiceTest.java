package com.medical.schoolMedical.modules.healthcheck.services;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckScheduleDTO;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import com.medical.schoolMedical.modules.healthcheck.mappers.HealthCheckConsentMapper;
import com.medical.schoolMedical.modules.healthcheck.mappers.HealthCheckScheduleMapper;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckConsentRepository;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckScheduleRepository;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.mappers.StudentMapper;
import com.medical.schoolMedical.modules.user_management.repositories.StudentRepository;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckConsentServiceTest {

    @Mock
    private StudentService studentService;
    @Mock
    private HealthCheckConsentMapper healthCheckConsentMapper;
    @Mock
    private StudentMapper studentMapper;
    @Mock
    private HealthCheckScheduleMapper healthCheckScheduleMapper;
    @Mock
    private HealthCheckConsentRepository healthCheckConsentRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private HealthCheckScheduleRepository healthCheckScheduleRepository;

    @InjectMocks
    private HealthCheckConsentService healthCheckConsentService;

    @Test
    @DisplayName("Should send health check schedule to parents and save all consents via batch")
    void testSendCheckScheduleToParentSuccess() {
        HealthCheckScheduleDTO scheduleDTO = new HealthCheckScheduleDTO();
        scheduleDTO.setId(1L);

        HealthCheckSchedule schedule = new HealthCheckSchedule();
        schedule.setId(1L);
        schedule.setClassName(3);

        Parent parent = new Parent();
        parent.setId(10L);

        Student s1 = new Student();
        s1.setId(101L);
        s1.setParent(parent);

        Student s2 = new Student();
        s2.setId(102L);
        s2.setParent(parent);

        when(healthCheckScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(healthCheckScheduleRepository.save(any(HealthCheckSchedule.class))).thenReturn(schedule);
        when(healthCheckScheduleMapper.toHealthCheckSchedule(scheduleDTO)).thenReturn(schedule);
        when(studentRepository.findByClassNameStartingWith("3")).thenReturn(List.of(s1, s2));

        healthCheckConsentService.sendCheckSchedule_toParent(scheduleDTO);

        assertTrue(schedule.isSentToParent());
        assertNotNull(schedule.getSentDate());

        verify(healthCheckScheduleRepository, times(1)).save(schedule);
        verify(healthCheckConsentRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw BusinessException when schedule ID does not exist")
    void testSendCheckScheduleToParentNotFound() {
        HealthCheckScheduleDTO scheduleDTO = new HealthCheckScheduleDTO();
        scheduleDTO.setId(999L);

        when(healthCheckScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            healthCheckConsentService.sendCheckSchedule_toParent(scheduleDTO);
        });

        assertEquals(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS, ex.getErrorCode());
        verify(healthCheckConsentRepository, never()).saveAll(anyList());
    }
}
