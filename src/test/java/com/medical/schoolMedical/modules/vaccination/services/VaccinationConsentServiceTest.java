package com.medical.schoolMedical.modules.vaccination.services;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationScheduleDTO;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationSchedule;
import com.medical.schoolMedical.modules.vaccination.mappers.VaccinationConsentMapper;
import com.medical.schoolMedical.modules.vaccination.mappers.VaccinationScheduleMapper;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationConsentRepository;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaccinationConsentServiceTest {

    @Mock
    private VaccinationConsentRepository vaccinationConsentRepository;
    @Mock
    private VaccinationScheduleMapper vaccinationScheduleMapper;
    @Mock
    private VaccinationScheduleRepository vaccinationScheduleRepository;
    @Mock
    private VaccinationConsentMapper vaccinationConsentMapper;
    @Mock
    private StudentService studentService;
    @Mock
    private VaccinationScheduleService vaccinationScheduleService;

    @InjectMocks
    private VaccinationConsentService vaccinationConsentService;

    @Test
    @DisplayName("Should send vaccination schedule to parents and save all consents via batch")
    void testSendVaccinationScheduleToParentSuccess() {
        VaccinationScheduleDTO dto = new VaccinationScheduleDTO();
        dto.setId(1L);

        VaccinationSchedule schedule = new VaccinationSchedule();
        schedule.setId(1L);
        schedule.setSentToParent(false);

        Parent parent = new Parent();
        parent.setId(5L);

        Student s1 = new Student();
        s1.setId(201L);
        s1.setParent(parent);

        when(vaccinationScheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(vaccinationScheduleRepository.save(any(VaccinationSchedule.class))).thenReturn(schedule);
        when(vaccinationScheduleMapper.toVaccinationSchedule(dto)).thenReturn(schedule);
        when(studentService.getAllStudents()).thenReturn(List.of(s1));

        vaccinationConsentService.sendVaccinationSchedule_toParent(dto);

        assertTrue(schedule.isSentToParent());
        assertNotNull(schedule.getSentDate());

        verify(vaccinationScheduleRepository, times(1)).save(schedule);
        verify(vaccinationConsentRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Should throw BusinessException when vaccination schedule does not exist")
    void testSendVaccinationScheduleNotFound() {
        VaccinationScheduleDTO dto = new VaccinationScheduleDTO();
        dto.setId(999L);

        when(vaccinationScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            vaccinationConsentService.sendVaccinationSchedule_toParent(dto);
        });

        assertEquals(ErrorCode.VACCINATION_SCHEDULE_NOT_EXISTS, ex.getErrorCode());
        verify(vaccinationConsentRepository, never()).saveAll(anyList());
    }
}
