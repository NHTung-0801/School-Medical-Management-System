package com.medical.schoolMedical.service;

import com.medical.schoolMedical.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.dto.VaccinationRecordDTO;
import com.medical.schoolMedical.entities.*;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.mapper.VaccinationConsentMapper;
import com.medical.schoolMedical.mapper.VaccinationRecordMapper;
import com.medical.schoolMedical.repositories.SchoolNurseRepository;
import com.medical.schoolMedical.repositories.VaccinationRecordRepository;
import com.medical.schoolMedical.repositories.VaccinationScheduleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VaccinationRecordService {
    VaccinationRecordRepository vaccinationRecordRepository;
    VaccinationScheduleService service;
    VaccinationRecordMapper vaccinationRecordMapper;
    SchoolNurseRepository schoolNurseRepository;
    VaccinationConsentService vaccinationConsentService;
    VaccinationConsentMapper vaccinationConsentMapper;

    private VaccinationRecord vaccinationRecord_fullInfor(VaccinationRecordDTO vaccinationRecordDTO, Long userID){
        //Chuyển kiểu để lưu form:
        VaccinationRecord vaccinationRecord =  vaccinationRecordMapper.toVaccinationRecord(vaccinationRecordDTO);

//        tìm school nurse phù hơp và gán vào trường schoolNurse trong đối tượng vaccinationRecord
        SchoolNurse schoolNurse = schoolNurseRepository.findByUser_Id(userID).orElseThrow(()->
                new BusinessException(ErrorCode.SCHOOL_NURSE_NOT_EXISTS));
//        Gán lại
        vaccinationRecord.setSchoolNurse(schoolNurse);
        return vaccinationRecord;
    }

    public void create_VaccinationRecord(VaccinationRecordDTO vaccinationRecordDTO,Long vaccinationConsentID, Long userID) {
        VaccinationConsent vaccinationConsent = null;
        VaccinationRecord vaccinationRecord = null;
        try{
            vaccinationConsent = vaccinationConsentService.getVaccinationConsentEntity_ById(vaccinationConsentID);
        }catch (BusinessException e){
            throw new BusinessException(e.getErrorCode());
        }

        try{
            vaccinationRecord = vaccinationRecord_fullInfor(vaccinationRecordDTO, userID);
            vaccinationRecord.setVaccinationConsent(vaccinationConsent);
//            Cập nhật bản vaccination consent tương ứng để biết học sinh đó đã có bản ghi kết quả
            vaccinationRecord.getVaccinationConsent().setVaccinated(true);
            log.info("Dữ liệu của đối tượng vaccinationRecord trong create: {}", vaccinationRecord);

            try{
                VaccinationRecord result = vaccinationRecordRepository.save(vaccinationRecord);
                log.info("Save VaccinationRecord==> "+result);
            }catch (Exception e){
                throw new BusinessException(ErrorCode.SAVE_VACCINATION_RECORD_FAILED);
            }
        }catch (BusinessException e){
            throw new BusinessException(e.getErrorCode());
        }


    }

    //Lấy bản ghi record tương ứng
    private VaccinationRecord toVaccinationRecord(VaccinationConsent findByVaccinationConsent) {
        return vaccinationRecordRepository.
                findByVaccinationConsent(findByVaccinationConsent)
                .orElseThrow(()->new BusinessException(ErrorCode.VACCINATION_RECORD_NOT_EXISTS));

    }

    //    Kiểm tra bản đã có record của bản consent chưa
    public VaccinationRecordDTO toVaccinationRecordDTO(VaccinationConsentDTO vaccinationConsentDTO) {
        VaccinationConsent vaccinationConsent = vaccinationConsentMapper.toVaccinationConsent(vaccinationConsentDTO);
        VaccinationRecord vaccinationRecord;
        try{
            vaccinationRecord = toVaccinationRecord(vaccinationConsent);
            return vaccinationRecordMapper.toVaccinationRecordDTO(vaccinationRecord);
        }catch (BusinessException e){
            throw new BusinessException(e.getErrorCode());

        }

    }

    public void update_VaccinationRecord(VaccinationRecordDTO vaccinationRecordDTO, Long userId) {
        VaccinationRecord vaccinationRecord = null;
        try {
            vaccinationRecord = vaccinationRecord_fullInfor(vaccinationRecordDTO, userId);
            log.info("Dữ liệu của đối tượng vaccinationRecord trong update: {}", vaccinationRecord);
        }catch (BusinessException e){
            throw new BusinessException(ErrorCode.SCHOOL_NURSE_NOT_EXISTS);
        }
        //    Giữ cho is_vaccinated của bản ghi đó vẫn là true nếu không nó tự động chuyển false làm sai dữ liệu
        vaccinationRecord.getVaccinationConsent().setVaccinated(true);
//        lấy bản ghi cần update:
        VaccinationRecord vaccinationRecord_needUpdate = vaccinationRecordRepository.findById(vaccinationRecord.getId())
                .orElseThrow(()->new BusinessException(ErrorCode.VACCINATION_RECORD_NOT_EXISTS));
        log.info("Dữ liệu của đối tượng vaccinationRecord_needUpdate: {}", vaccinationRecord_needUpdate);

//        Tiến hành gán dữ liệu để update
        vaccinationRecordMapper.updateVaccinationRecord(vaccinationRecord_needUpdate, vaccinationRecord);
        try{
            VaccinationRecord result = vaccinationRecordRepository.save(vaccinationRecord_needUpdate);
            log.info("Save VaccinationRecord ==> "+result);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_VACCINATION_RECORD_FAILED);
        }
    }

    // Gửi cho phụ huynh:
    public void sendRecordsToParents(List<Long> consentIds){
        List<VaccinationRecord> recordsToUpdate = vaccinationRecordRepository.findByConsentIds(consentIds);
        if (recordsToUpdate.size() != consentIds.size()) {
            throw new BusinessException(ErrorCode.VACCINATION_RECORD_NOT_EXISTS);
        }
        recordsToUpdate.forEach(record -> record.setSentToParent(true));
        vaccinationRecordRepository.saveAll(recordsToUpdate);

    }

    //    Lấy danh sách các record đã được gửi đến phụ huynh
    public Page<VaccinationRecordDTO> getSentRecordsToParents(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, 20);

        Page<VaccinationRecord> sentRecords = vaccinationRecordRepository.findBySentToParentTrueAndVaccinationConsent_Parent_User_Id(userId, pageable);

        Page<VaccinationRecordDTO> sentRecordsDTO = sentRecords.map(vaccinationRecordMapper::toVaccinationRecordDTO);
        log.info("list record Sent to parents in vaccinationRecordService: {}", sentRecordsDTO.getContent());
        return sentRecordsDTO;
    }

    //    Lấy vaccination record tương ứng và cập nhật nó là parent đã xem:
    public VaccinationRecordDTO getVaccinationRecord_updateViewed(long id){
        VaccinationRecord vaccinationRecord = vaccinationRecordRepository.findById(id)
                .orElseThrow(()->new BusinessException(ErrorCode.VACCINATION_RECORD_NOT_EXISTS));

//        update viewedByParent to true
        vaccinationRecord.setViewedByParent(true);
        try{
            vaccinationRecordRepository.save(vaccinationRecord);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_VACCINATION_RECORD_FAILED);
        }

        return vaccinationRecordMapper.toVaccinationRecordDTO(vaccinationRecord);
    }

}
