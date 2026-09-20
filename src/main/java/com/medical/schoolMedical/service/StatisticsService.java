package com.medical.schoolMedical.service;

import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckScheduleRepository;
import com.medical.schoolMedical.modules.medical_event.repositories.MedicalEventRepository;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineRepository;
import com.medical.schoolMedical.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatisticsService {
    @Autowired
    private HealthCheckScheduleRepository healthCheckScheduleRepository;
    @Autowired
    private VaccinationScheduleRepository vaccinationScheduleRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ParentRepository parentRepository;
    @Autowired
    private SchoolNurseRepository schoolNurseRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private MedicalEventRepository medicalEventRepository;


    public List<Integer> getMonthlyVaccinationCounts(int year) {
        List<Object[]> rawStats = vaccinationScheduleRepository.getMonthlyVaccinationStats(year);
        return rawStats.stream()
                .map(row -> ((Number) row[1]).intValue())
                .toList();
    }

    public List<Integer> getMonthlyHealthCheckCounts(int year) {
        List<Object[]> rawStats = healthCheckScheduleRepository.getMonthlyHealthCheckStats(year);
        return rawStats.stream()
                .map(row -> ((Number) row[1]).intValue())
                .toList();
    }

    public List<Integer> getMonthlyMedicalEventCounts(int year) {
        List<Object[]> rawStats = medicalEventRepository.getMonthlyMedicalEventStats(year);
        return rawStats.stream()
                .map(row -> ((Number) row[1]).intValue())
                .toList();
    }

    public Map<String, Long> getUserCountsByRole() {
        long studentCount = studentRepository.count();
        long parentCount = parentRepository.count();
        long nurseCount = schoolNurseRepository.count();
        long medicineCount = medicineRepository.count();

        return Map.of(
                "students", studentCount,
                "parents", parentCount,
                "nurses", nurseCount,
                "medicines", medicineCount
        );
    }



}
