package com.medical.schoolMedical.modules.user_management.services;

import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckScheduleRepository;
import com.medical.schoolMedical.modules.medical_event.repositories.MedicalEventRepository;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineRepository;
import com.medical.schoolMedical.modules.user_management.repositories.ParentRepository;
import com.medical.schoolMedical.modules.user_management.repositories.SchoolNurseRepository;
import com.medical.schoolMedical.modules.user_management.repositories.StudentRepository;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private HealthCheckScheduleRepository healthCheckScheduleRepository;
    @Mock
    private VaccinationScheduleRepository vaccinationScheduleRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private ParentRepository parentRepository;
    @Mock
    private SchoolNurseRepository schoolNurseRepository;
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private MedicalEventRepository medicalEventRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    @DisplayName("Should aggregate user counts by role accurately")
    void testGetUserCountsByRole() {
        when(studentRepository.count()).thenReturn(150L);
        when(parentRepository.count()).thenReturn(120L);
        when(schoolNurseRepository.count()).thenReturn(5L);
        when(medicineRepository.count()).thenReturn(45L);

        Map<String, Long> counts = statisticsService.getUserCountsByRole();

        assertNotNull(counts);
        assertEquals(150L, counts.get("students"));
        assertEquals(120L, counts.get("parents"));
        assertEquals(5L, counts.get("nurses"));
        assertEquals(45L, counts.get("medicines"));
    }

    @Test
    @DisplayName("Should extract monthly vaccination counts for given year")
    void testGetMonthlyVaccinationCounts() {
        List<Object[]> mockStats = List.of(
                new Object[]{1, 10},
                new Object[]{2, 25},
                new Object[]{3, 15}
        );
        when(vaccinationScheduleRepository.getMonthlyVaccinationStats(2026)).thenReturn(mockStats);

        List<Integer> counts = statisticsService.getMonthlyVaccinationCounts(2026);

        assertNotNull(counts);
        assertEquals(3, counts.size());
        assertEquals(10, counts.get(0));
        assertEquals(25, counts.get(1));
        assertEquals(15, counts.get(2));
    }
}
