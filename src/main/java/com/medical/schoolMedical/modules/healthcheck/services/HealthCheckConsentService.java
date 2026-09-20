package com.medical.schoolMedical.modules.healthcheck.services;

import com.medical.schoolMedical.modules.user_management.dto.StudentDTO;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.user_management.mappers.StudentMapper;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckConsentDTO;
import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckScheduleDTO;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import com.medical.schoolMedical.modules.healthcheck.mappers.HealthCheckConsentMapper;
import com.medical.schoolMedical.modules.healthcheck.mappers.HealthCheckScheduleMapper;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckConsentRepository;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckScheduleRepository;
import com.medical.schoolMedical.modules.user_management.repositories.StudentRepository;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthCheckConsentService {
    final StudentService studentService;
    final HealthCheckConsentMapper healthCheckConsentMapper;
    final StudentMapper studentMapper;
    final HealthCheckScheduleMapper healthCheckScheduleMapper;
    final HealthCheckConsentRepository healthCheckConsentRepository;
    final StudentRepository studentRepository;
    final HealthCheckScheduleRepository healthCheckScheduleRepository;

    public HealthCheckConsentService(
            StudentService studentService,
            HealthCheckConsentMapper healthCheckConsentMapper,
            StudentMapper studentMapper,
            HealthCheckScheduleMapper healthCheckScheduleMapper,
            HealthCheckConsentRepository healthCheckConsentRepository,
            StudentRepository studentRepository,
            HealthCheckScheduleRepository healthCheckScheduleRepository
    ) {
        this.studentService = studentService;
        this.healthCheckConsentMapper = healthCheckConsentMapper;
        this.studentMapper = studentMapper;
        this.healthCheckScheduleMapper = healthCheckScheduleMapper;
        this.healthCheckConsentRepository = healthCheckConsentRepository;
        this.studentRepository = studentRepository;
        this.healthCheckScheduleRepository = healthCheckScheduleRepository;
    }

    // Gửi lịch đến phụ huynh:
    @Transactional
    public void sendCheckSchedule_toParent(HealthCheckScheduleDTO healthCheckScheduleDTO) {
        HealthCheckSchedule healthCheckScheduleUpdate = healthCheckScheduleRepository.findById(healthCheckScheduleDTO.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));

        healthCheckScheduleUpdate.setSentToParent(true);
        healthCheckScheduleUpdate.setSentDate(LocalDate.now());

        try {
            healthCheckScheduleRepository.save(healthCheckScheduleUpdate);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_SCHEDULE_FAILED);
        }

        HealthCheckSchedule healthCheckSchedule = healthCheckScheduleMapper.toHealthCheckSchedule(healthCheckScheduleDTO);

        // Lấy khối cần khám để gửi lịch cho các phụ huynh có con ở khối đó
        String classPrefix = String.valueOf(healthCheckSchedule.getClassName());

        List<Student> students = studentRepository.findByClassNameStartingWith(classPrefix);
        List<HealthCheckConsent> consentList = new ArrayList<>();
        for (Student student : students) {
            HealthCheckConsent consent = createConsent(healthCheckSchedule, student);
            consentList.add(consent);
        }
        try {
            healthCheckConsentRepository.saveAll(consentList);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_CONSENT_FAILED);
        }
    }

    // Dùng để tạo bản ghi consent cho từng học sinh
    private HealthCheckConsent createConsent(HealthCheckSchedule healthCheckSchedule, Student student) {
        return HealthCheckConsent.builder()
                .schedule(healthCheckSchedule)
                .student(student)
                .parent(student.getParent())
                .status(ConsentStatus.UNCONFIRMED)
                .build();
    }

    // Phân trang:
    private Pageable pagination(int page, int size) {
        return PageRequest.of(page, size);
    }

    // Lấy danh sách các consent của phụ huynh tương ứng
    public Page<HealthCheckConsentDTO> getHealthCheckConsentByParentId(Long userId, int page) {
        Pageable pageable = pagination(page, 10);

        Page<HealthCheckConsent> healthCheckConsents = healthCheckConsentRepository.findByParent_User_IdOrderByIdDesc(userId, pageable);

        Page<HealthCheckConsentDTO> consentDTOPage = healthCheckConsents.map(healthCheckConsentMapper::toDTO);
        log.info("consentDTOPage in getHealthCheckConsentByParentId in consentService: {}", consentDTOPage.getContent());
        return consentDTOPage;
    }

    // Lấy thông tin của phiếu khám sức khỏe hiển thị bên phía phụ huynh
    public HealthCheckConsentDTO getHealthCheckConsentById(Long healthCheckID) {
        HealthCheckConsent healthCheck = healthCheckConsentRepository.findById(healthCheckID)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        return healthCheckConsentMapper.toDTO(healthCheck);
    }

    // Xử lí trạng thái phiếu khám khi người dùng đồng ý hoặc không
    public void updateHealthCheckConsent(Long id, String response) {
        HealthCheckConsent healthCheckConsent = healthCheckConsentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        if (healthCheckConsent.getSchedule().getCheckDate().isBefore(LocalDateTime.now()) || "OVERDUE".equals(healthCheckConsent.getStatus().name())) {
            throw new BusinessException(ErrorCode.SURVEY_EXPIRED);
        }
        if ("agree".equals(response)) {
            healthCheckConsent.setStatus(ConsentStatus.ACCEPTED);
        } else if ("disagree".equals(response)) {
            healthCheckConsent.setStatus(ConsentStatus.DECLINED);
        }
        try {
            healthCheckConsentRepository.save(healthCheckConsent);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_CONSENT_FAILED);
        }
    }

    // Tự động cập nhật trạng thái phiếu hết hạn nếu quá ngày
    @Transactional
    public void update_SurveyExpired_HealthCheckConsent() {
        List<HealthCheckConsent> unconfirms = healthCheckConsentRepository.findByStatusWithSchedule(ConsentStatus.UNCONFIRMED);
        List<HealthCheckConsent> toUpdate = new ArrayList<>();
        for (HealthCheckConsent healthCheckConsent : unconfirms) {
            if (healthCheckConsent.getSchedule().getCheckDate().isBefore(LocalDateTime.now())) {
                healthCheckConsent.setStatus(ConsentStatus.OVERDUE);
                toUpdate.add(healthCheckConsent);
            }
        }
        healthCheckConsentRepository.saveAll(toUpdate);
    }

    // Kiểm tra người dùng có phiếu khám sức khỏe nào không
    public boolean hasNewNotification(Long id) {
        return healthCheckConsentRepository.existsByParent_IdAndStatus(id, ConsentStatus.UNCONFIRMED);
    }

    // Lấy toàn bộ học sinh đã được accepted theo ngày kiểm tra sức khỏe đã khám và chưa khám
    public Page<HealthCheckConsentDTO> getStudentsHealthCheck(Long scheduleId, int page, boolean is_checked_health) {
        Pageable pageable = pagination(page, 10);

        HealthCheckSchedule schedule = healthCheckScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));
        log.info("schedule in getStudentsHealthCheck: {}", schedule);

        Page<HealthCheckConsent> listConsent = healthCheckConsentRepository
                .listConsentsByScheduleAndStatusAndCheckState(schedule, ConsentStatus.ACCEPTED, is_checked_health, pageable);

        log.info("listConsent in getStudentsHealthCheck: {}", listConsent.getContent());

        return listConsent.map(healthCheckConsentMapper::toDTO);
    }

    // Lấy health check consent phù hợp với cái người dùng chọn
    public HealthCheckConsentDTO getHealthCheckConsentDTO_ById(Long healthCheckID) {
        HealthCheckConsent consent = healthCheckConsentRepository.findById(healthCheckID)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        return healthCheckConsentMapper.toDTO(consent);
    }

    public HealthCheckConsent getHealthCheckConsentEntity_ById(Long healthCheckID) {
        return healthCheckConsentRepository.findById(healthCheckID)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));
    }

    // Lấy toàn bộ học sinh đã được accepted theo ngày kiểm tra sức khỏe đã khám và cần đặt lịch tư vấn sức khỏe
    public Page<HealthCheckConsentDTO> getStudentsHealthCheck_needsConsultation(Long scheduleId, int page, boolean is_checked_health) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

        HealthCheckSchedule schedule = healthCheckScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));
        log.info("schedule in getStudentsHealthCheck_needsConsultation: {}", schedule);

        Page<HealthCheckConsent> listConsent = healthCheckConsentRepository
                .findByScheduleStatusCheckStateWithConsultation(schedule, ConsentStatus.ACCEPTED, is_checked_health, pageable);

        log.info("listConsent in getStudentsHealthCheck: {}", listConsent.getContent());

        return listConsent.map(healthCheckConsentMapper::toDTO);
    }
}
