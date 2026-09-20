package com.medical.schoolMedical.service;

import com.medical.schoolMedical.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.dto.VaccinationScheduleDTO;
import com.medical.schoolMedical.entities.*;
import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.mapper.VaccinationConsentMapper;
import com.medical.schoolMedical.mapper.VaccinationScheduleMapper;
import com.medical.schoolMedical.repositories.StudentRepository;
import com.medical.schoolMedical.repositories.VaccinationConsentRepository;
import com.medical.schoolMedical.repositories.VaccinationScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VaccinationConsentService {
    VaccinationConsentRepository vaccinationConsentRepository;
    VaccinationScheduleMapper vaccinationScheduleMapper;
    VaccinationScheduleRepository vaccinationScheduleRepository;
    VaccinationConsentMapper vaccinationConsentMapper;
    StudentService studentService;
    VaccinationScheduleService vaccinationScheduleService;

    //    Gửi lịch đến phụ huynh:
    @Transactional
    public void sendVaccinationSchedule_toParent(VaccinationScheduleDTO vaccinationScheduleDTO){
//        Lấy VaccinationSchedule tương ứng để cập nhật trạng thái dãd gửi cho parent
        VaccinationSchedule vaccinationScheduleUpdate = vaccinationScheduleRepository.findById(vaccinationScheduleDTO.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.VACCINATION_SCHEDULE_NOT_EXISTS));

        vaccinationScheduleUpdate.setSentToParent(true);
        vaccinationScheduleUpdate.setSentDate(LocalDate.now());

        try {
            vaccinationScheduleRepository.save(vaccinationScheduleUpdate);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_VACCINATION_SCHEDULE_FAILED);
        }

        VaccinationSchedule vaccinationSchedule = vaccinationScheduleMapper.toVaccinationSchedule(vaccinationScheduleDTO);
        List<Student> students = studentService.getAllStudents();
        List<VaccinationConsent> consentList = new ArrayList<>();
        for(Student student : students){
            VaccinationConsent consent = createVaccinationConsent(vaccinationSchedule,student);
           consentList.add(consent);
        }
        try {
            vaccinationConsentRepository.saveAll(consentList);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_VACCINATION_CONSENT_FAILED);
        }
    }

    //    dungf để tạo bản ghi consent cho từng học sinh
    private VaccinationConsent createVaccinationConsent(VaccinationSchedule vaccinationSchedule, Student student) {
        return VaccinationConsent.builder()
                .schedule(vaccinationSchedule)
                .student(student)
                .parent(student.getParent())
                .status(ConsentStatus.UNCONFIRMED)
                .build();
    }

    //    phiếu tiêm chủng chi tiết của từng student
    public VaccinationConsentDTO getVaccinationConsentDTOById(Long vaccinationConsentID) {
        VaccinationConsent consent =  vaccinationConsentRepository.findById(vaccinationConsentID)
                .orElseThrow(() -> new BusinessException(ErrorCode.VACCINATION_CONSENT_NOT_EXISTS));

        return  vaccinationConsentMapper.toVaccinationConsentDTO(consent);
    }

    //    Xử lí trạng thái phiếu tiêm khi người dùng đồng ý hoặc không
    public void updateVaccinationConsent(Long id, String response) {
        VaccinationConsent consent = vaccinationConsentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.VACCINATION_CONSENT_NOT_EXISTS));

        if(consent.getSchedule().getInjectionDate().isBefore(LocalDateTime.now()) || "OVERDUE".equals(consent.getStatus().name())){
            throw new BusinessException(ErrorCode.SURVEY_EXPIRED);
        }
        if("agree".equals(response)){
            consent.setStatus(ConsentStatus.ACCEPTED);
        }else if("disagree".equals(response)){
            consent.setStatus(ConsentStatus.DECLINED);
        }
        try{
            vaccinationConsentRepository.save(consent);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_VACCINATION_CONSENT_FAILED);
        }

    }

//    Hàm tự động cập nhật trạng thái phiêú hết hạn nếu quá ngày
    @Transactional
    public void update_SurveyExpired_VaccinationConsent() {
        List<VaccinationConsent> unconfirms = vaccinationConsentRepository.findByStatusWithSchedule(ConsentStatus.UNCONFIRMED);
        List<VaccinationConsent> toUpdate = new ArrayList<>();
        for(VaccinationConsent vaccinationConsent : unconfirms){
            if(vaccinationConsent.getSchedule().getInjectionDate().isBefore(LocalDateTime.now())){
                vaccinationConsent.setStatus(ConsentStatus.OVERDUE);
                toUpdate.add(vaccinationConsent);
            }
        }
        vaccinationConsentRepository.saveAll(toUpdate);
    }

//    //    Kiểm tra người dùng có phiêú khám sức khỏe nào không
//    public boolean hasNewNotification(Long id) {
//        return healthCheckConsentRepository.existsByParent_IdAndStatus(id, ConsentStatus.UNCONFIRMED);
//
//    }

    //    lấy danh sách các VaccinationConsent của phụ huynh tương ứng
    public Page<VaccinationConsentDTO> getVaccinationConsentByParentId(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 20); // Lấy trang đầu tiên với 20 bản ghi

        Page<VaccinationConsent> vaccinationConsents = vaccinationConsentRepository.findByParent_User_IdOrderByIdDesc(userId,pageable);

//        CHuyển qua Page<VaccinationConsentDTO>
        Page<VaccinationConsentDTO> consentDTOPage = vaccinationConsents.map(vaccinationConsentMapper::toVaccinationConsentDTO);
        log.info("consentDTOPage in getVaccinationConsentByParentId in consentService: {}", consentDTOPage.getContent());
        return consentDTOPage;
    }

    //    Lấy toàn bộ học sinh theo ngày tiêm, đã tiêm và chưa tiêm
    public Page<VaccinationConsentDTO> getStudentsVaccination(Long scheduleId, int page, boolean is_vaccinated) {

        Pageable pageable = PageRequest.of(page, 20);

//        Lấy lịch phù hợp với scheduleId
        VaccinationSchedule schedule =  null;
        try{
            VaccinationScheduleDTO scheduleDTO = vaccinationScheduleService.getVaccinationScheduleById(scheduleId);
            schedule = vaccinationScheduleMapper.toVaccinationSchedule(scheduleDTO);
        }catch (BusinessException e){
            throw new BusinessException(e.getErrorCode());
        }
        log.info("schedule in getStudentsVaccination: {}", schedule);


//        Lấy danh sách student theo consent, dùng consent để truy suất student tương ứng
        Page<VaccinationConsent> listConsent = vaccinationConsentRepository
                .listConsentsByScheduleAndStatusAndCheckState(schedule, ConsentStatus.ACCEPTED,is_vaccinated, pageable);

        log.info("listConsent in getStudentsVaccination: {}", listConsent.getContent());

//        CHuyển qua Page<VaccinationConsentDTO>
        Page<VaccinationConsentDTO> consentDTOPage = listConsent.map(vaccinationConsentMapper::toVaccinationConsentDTO);
        return consentDTOPage;
    }

    public VaccinationConsent getVaccinationConsentEntity_ById(Long consentID) {
        VaccinationConsent vaccinationConsent =  vaccinationConsentRepository.findById(consentID)
                .orElseThrow(() -> new BusinessException(ErrorCode.VACCINATION_CONSENT_NOT_EXISTS));

        return  vaccinationConsent;
    }

    public VaccinationConsentDTO getVaccinationConsentDTO_ById(Long consentID) {
        VaccinationConsent vaccinationConsent =  vaccinationConsentRepository.findById(consentID)
                .orElseThrow(() -> new BusinessException(ErrorCode.VACCINATION_CONSENT_NOT_EXISTS));

        return vaccinationConsentMapper.toVaccinationConsentDTO(vaccinationConsent);

    }





}
