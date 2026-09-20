package com.medical.schoolMedical.modules.healthcheck.services;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckScheduleDTO;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import com.medical.schoolMedical.modules.healthcheck.mappers.HealthCheckScheduleMapper;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckScheduleRepository;
import com.medical.schoolMedical.modules.user_management.repositories.SchoolNurseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HealthCheckScheduleService {
    final HealthCheckScheduleRepository healthCheckScheduleRepository;
    final HealthCheckScheduleMapper healthCheckScheduleMapper;
    final SchoolNurseRepository schoolNurseRepository;

    @Lazy
    @Autowired
    HealthCheckConsentService healthCheckConsentService;

    public HealthCheckScheduleDTO create_checkSchedule(HealthCheckScheduleDTO healthCheckScheduleDTO, Long userId) {
        if (healthCheckScheduleDTO.getCheckDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.CHECK_DATE_INVALID);
        }

        HealthCheckSchedule healthCheckSchedule = healthCheckScheduleMapper.toHealthCheckSchedule(healthCheckScheduleDTO);

        // Lấy Nurse phù hợp với userId
        SchoolNurse nurse = schoolNurseRepository.findByUser_Id(userId).orElseThrow(() ->
                new BusinessException(ErrorCode.SCHOOL_NURSE_NOT_EXISTS));

        healthCheckSchedule.setNurse(nurse);

        try {
            return healthCheckScheduleMapper.toHealthCheckScheduleDTO(healthCheckScheduleRepository.save(healthCheckSchedule));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_SCHEDULE_FAILED);
        }
    }

    // Lấy các lịch khám sức khỏe đã gửi
    public Page<HealthCheckScheduleDTO> getAllHealthCheckSchedule_sent(int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "id"));
        boolean isSentToParent = true;

        Page<HealthCheckSchedule> vaccinationSchedules = healthCheckScheduleRepository.findBySentToParent(isSentToParent, pageable);
        Page<HealthCheckScheduleDTO> healthCheckScheduleDTOPage = vaccinationSchedules.map(healthCheckScheduleMapper::toHealthCheckScheduleDTO);
        log.info("healthCheckScheduleDTOPage in getAllHealthCheckSchedule_sent: {}", healthCheckScheduleDTOPage.getContent());
        return healthCheckScheduleDTOPage;
    }

    // Lấy các lịch tiêm chủng đã tạo nhưng chưa gửi
    public Page<HealthCheckScheduleDTO> getAllHealthCheckSchedule_drafts(int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "id"));
        boolean isSentToParent = false;

        Page<HealthCheckSchedule> vaccinationSchedules = healthCheckScheduleRepository.findBySentToParent(isSentToParent, pageable);
        Page<HealthCheckScheduleDTO> healthCheckScheduleDTOPage = vaccinationSchedules.map(healthCheckScheduleMapper::toHealthCheckScheduleDTO);
        log.info("healthCheckScheduleDTOPage in getAllHealthCheckSchedule_drafts: {}", healthCheckScheduleDTOPage.getContent());
        return healthCheckScheduleDTOPage;
    }

    // Lấy Schedule theo id
    public HealthCheckScheduleDTO getHealthCheckScheduleById(Long id) {
        HealthCheckSchedule healthCheckSchedule = healthCheckScheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));
        return healthCheckScheduleMapper.toHealthCheckScheduleDTO(healthCheckSchedule);
    }
}
