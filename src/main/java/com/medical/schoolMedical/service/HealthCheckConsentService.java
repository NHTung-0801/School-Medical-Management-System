package com.medical.schoolMedical.service;

import com.medical.schoolMedical.dto.HealthCheckConsentDTO;
import com.medical.schoolMedical.dto.HealthCheckRecordDTO;
import com.medical.schoolMedical.dto.HealthCheckScheduleDTO;
import com.medical.schoolMedical.dto.StudentDTO;
import com.medical.schoolMedical.entities.HealthCheckConsent;
import com.medical.schoolMedical.entities.HealthCheckSchedule;
import com.medical.schoolMedical.entities.Student;
import com.medical.schoolMedical.entities.VaccinationSchedule;
import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.mapper.HealthCheckConsentMapper;
import com.medical.schoolMedical.mapper.HealthCheckRecordMapper;
import com.medical.schoolMedical.mapper.HealthCheckScheduleMapper;
import com.medical.schoolMedical.mapper.StudentMapper;
import com.medical.schoolMedical.repositories.HealthCheckConsentRepository;
import com.medical.schoolMedical.repositories.HealthCheckRecordRepository;
import com.medical.schoolMedical.repositories.HealthCheckScheduleRepository;
import com.medical.schoolMedical.repositories.StudentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

//    Gửi lịch đến phụ huynh:
    public void sendCheckSchedule_toParent(HealthCheckScheduleDTO healthCheckScheduleDTO){

        //        Lấy HealthCheckSchedule tương ứng để cập nhật trạng thái dãd gửi cho parent
        HealthCheckSchedule healthCheckScheduleUpdate = healthCheckScheduleRepository.findById(healthCheckScheduleDTO.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));

        healthCheckScheduleUpdate.setSentToParent(true);
        healthCheckScheduleUpdate.setSentDate(LocalDate.now());

        try {
            healthCheckScheduleRepository.save(healthCheckScheduleUpdate);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_SCHEDULE_FAILED);
        }

        HealthCheckSchedule healthCheckSchedule = healthCheckScheduleMapper.toHealthCheckSchedule(healthCheckScheduleDTO);

//        Lấy khối cần khám để gửi lịch cho các phụ huynh có con ở khối đó
        String classPrefix = String.valueOf(healthCheckSchedule.getClassName());

        List<Student> students = studentRepository.findByClassNameStartingWith(classPrefix);
        for(Student student : students){
            HealthCheckConsent consent = createConsent(healthCheckSchedule,student);
            try {
                healthCheckConsentRepository.save(consent);
            }catch (Exception e){
                throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_CONSENT_FAILED);
            }
        }
    }

//    dungf để tạo bản ghi consent cho từng học sinh
    private HealthCheckConsent createConsent(HealthCheckSchedule healthCheckSchedule, Student student) {
        return HealthCheckConsent.builder()
                .schedule(healthCheckSchedule)
                .student(student)
                .parent(student.getParent())
                .status(ConsentStatus.UNCONFIRMED)
                .build();
    }

    //    Phân trang:
    private Pageable pagination(int page, int size) {
        //        số lượng học sinh trên 1 trang danh sách
        int pageSize = size;
//        Định nghĩa Pageable
//        cần tham số là: trang a cần lấy và số lg hs trên 1 trang
        return PageRequest.of(page, pageSize);
    }

//    lấy danh sách các consent của phụ huynh tương ứng
    public Page<HealthCheckConsentDTO> getHealthCheckConsentByParentId(Long userId, int page) {
        Pageable pageable = pagination(page, 10); // Lấy trang đầu tiên với 20 bản ghi

        Page<HealthCheckConsent> healthCheckConsents = healthCheckConsentRepository.findByParent_User_IdOrderByIdDesc(userId,pageable);

//        CHuyển qua Page<HealthCheckConsentDTO>
        Page<HealthCheckConsentDTO> consentDTOPage = healthCheckConsents.map(healthCheckConsentMapper::toDTO);
        log.info("consentDTOPage in getHealthCheckConsentByParentId in consentService: {}", consentDTOPage.getContent());
        return consentDTOPage;
    }

//    Lấy thông tin của phiếu khám sức khỏe hiển thị bên phía phụ huynh
//    phiếu chi tiết của từng student
    public HealthCheckConsentDTO getHealthCheckConsentById(Long healthCheckID) {
        HealthCheckConsent healthCheck =  healthCheckConsentRepository.findById(healthCheckID)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        return  healthCheckConsentMapper.toDTO(healthCheck);
    }

//    Xử lí trạng thái phiếu khám khi người dùng đồng ý hoặc không
    public void updateHealthCheckConsent(Long id, String response) {
        HealthCheckConsent healthCheckConsent = healthCheckConsentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        if(healthCheckConsent.getSchedule().getCheckDate().isBefore(LocalDateTime.now()) || "OVERDUE".equals(healthCheckConsent.getStatus().name())){
            throw new BusinessException(ErrorCode.SURVEY_EXPIRED);
        }
        if("agree".equals(response)){
            healthCheckConsent.setStatus(ConsentStatus.ACCEPTED);
        }else if("disagree".equals(response)){
            healthCheckConsent.setStatus(ConsentStatus.DECLINED);
        }
        try{
            healthCheckConsentRepository.save(healthCheckConsent);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_HEALTH_CHECK_CONSENT_FAILED);
        }

    }

//    Hàm tự động cập nhật trạng thái phiêú hết hạn nếu quá ngày

    public void update_SurveyExpired_HealthCheckConsent() {
        List<HealthCheckConsent> unconfirms = healthCheckConsentRepository.findByStatusWithSchedule(ConsentStatus.UNCONFIRMED);
        List<HealthCheckConsent> toUpdate = new ArrayList<>();
        for(HealthCheckConsent healthCheckConsent : unconfirms){
            if(healthCheckConsent.getSchedule().getCheckDate().isBefore(LocalDateTime.now())){
                healthCheckConsent.setStatus(ConsentStatus.OVERDUE);
                toUpdate.add(healthCheckConsent);
            }
        }
        healthCheckConsentRepository.saveAll(toUpdate);

    }


//    Kiểm tra người dùng có phiêú khám sức khỏe nào không
    public boolean hasNewNotification(Long id) {
        return healthCheckConsentRepository.existsByParent_IdAndStatus(id, ConsentStatus.UNCONFIRMED);

    }

//    Lấy toàn bộ học sinh đã được accepted theo ngày kiểm tra sức khỏe đã khám và chưa khám
    public Page<HealthCheckConsentDTO> getStudentsHealthCheck(Long scheduleId, int page, boolean is_checked_health) {

        Pageable pageable = pagination(page, 10);

//        Lấy lịch phù hợp với scheduleId
        HealthCheckSchedule schedule = healthCheckScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));
        log.info("schedule in getStudentsHealthCheck: {}", schedule);


//        Lấy danh sách student theo consent, dùng consent để truy suất student tương ứng
        Page<HealthCheckConsent> listConsent = healthCheckConsentRepository
                .listConsentsByScheduleAndStatusAndCheckState(schedule, ConsentStatus.ACCEPTED,is_checked_health, pageable);

        log.info("listConsent in getStudentsHealthCheck: {}", listConsent.getContent());

//        CHuyển qua Page<HealthCheckConsentDTO>
        Page<HealthCheckConsentDTO> consentDTOPage = listConsent.map(healthCheckConsentMapper::toDTO);
        return consentDTOPage;
    }
//
//    Lấy health check consent phù hợp với cái người dùng chọn
    public HealthCheckConsentDTO getHealthCheckConsentDTO_ById(Long healthCheckID) {
        HealthCheckConsent consent = healthCheckConsentRepository.findById(healthCheckID)
                .orElseThrow(() -> new  BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        return healthCheckConsentMapper.toDTO(consent);

    }
//
     public HealthCheckConsent getHealthCheckConsentEntity_ById(Long healthCheckID) {
        HealthCheckConsent healthCheck =  healthCheckConsentRepository.findById(healthCheckID)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_CONSENT_NOT_FOUND));

        return  healthCheck;
    }

    //    Lấy toàn bộ học sinh đã được accepted theo ngày kiểm tra sức khỏe đã khám và cần đặt lịch tư vấn sức khỏe
    public Page<HealthCheckConsentDTO> getStudentsHealthCheck_needsConsultation(Long scheduleId, int page, boolean is_checked_health) {

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "id"));

//        Lấy lịch phù hợp với scheduleId
        HealthCheckSchedule schedule = healthCheckScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));
        log.info("schedule in getStudentsHealthCheck_needsConsultation: {}", schedule);


//        Lấy danh sách student theo consent, dùng consent để truy suất student tương ứng
        Page<HealthCheckConsent> listConsent = healthCheckConsentRepository
                .findByScheduleStatusCheckStateWithConsultation(schedule, ConsentStatus.ACCEPTED,is_checked_health, pageable);

        log.info("listConsent in getStudentsHealthCheck: {}", listConsent.getContent());

//        CHuyển qua Page<HealthCheckConsentDTO>
        Page<HealthCheckConsentDTO> consentDTOPage = listConsent.map(healthCheckConsentMapper::toDTO);
        return consentDTOPage;
    }

}

