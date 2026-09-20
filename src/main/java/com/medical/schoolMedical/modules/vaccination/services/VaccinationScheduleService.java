package com.medical.schoolMedical.modules.vaccination.services;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.vaccination.dto.VaccinationScheduleDTO;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationSchedule;
import com.medical.schoolMedical.modules.vaccination.mappers.VaccinationScheduleMapper;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationScheduleRepository;
import com.medical.schoolMedical.modules.user_management.repositories.SchoolNurseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class VaccinationScheduleService {
    final VaccinationScheduleRepository vaccinationScheduleRepository;
    final VaccinationScheduleMapper vaccinationScheduleMapper;
    final SchoolNurseRepository schoolNurseRepository;

    public VaccinationScheduleDTO createVaccinationSchedule(VaccinationScheduleDTO vaccinationScheduleDTO, Long userId) {
        VaccinationSchedule vaccinationSchedule = vaccinationScheduleMapper.toVaccinationSchedule(vaccinationScheduleDTO);
        log.info("vaccinationSchedule in createVaccinationSchedule: {}", vaccinationSchedule);

        // Lấy Nurse phù hợp với userId
        SchoolNurse nurse = schoolNurseRepository.findByUser_Id(userId).orElseThrow(() ->
                new BusinessException(ErrorCode.SCHOOL_NURSE_NOT_EXISTS));

        vaccinationSchedule.setNurse(nurse);

        try {
            VaccinationScheduleDTO result = vaccinationScheduleMapper.toVaccinationScheduleDTO(vaccinationScheduleRepository.save(vaccinationSchedule));
            return result;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_VACCINATION_SCHEDULE_FAILED);
        }
    }

    // Lấy các lịch tiêm chủng đã gửi
    public Page<VaccinationScheduleDTO> getAllVaccinationSchedule_sent(int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "id"));
        boolean isSentToParent = true;

        Page<VaccinationSchedule> vaccinationSchedules = vaccinationScheduleRepository.findBySentToParent(isSentToParent, pageable);
        Page<VaccinationScheduleDTO> vaccinationSchedulesDTOPage = vaccinationSchedules.map(vaccinationScheduleMapper::toVaccinationScheduleDTO);
        log.info("vaccinationSchedulesDTOPage in getAllVaccinationSchedule: {}", vaccinationSchedulesDTOPage.getContent());
        return vaccinationSchedulesDTOPage;
    }

    // Lấy các lịch tiêm chủng đã tạo nhưng chưa gửi
    public Page<VaccinationScheduleDTO> getAllVaccinationSchedule_drafts(int page) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "id"));
        boolean isSentToParent = false;

        Page<VaccinationSchedule> vaccinationSchedules = vaccinationScheduleRepository.findBySentToParent(isSentToParent, pageable);
        Page<VaccinationScheduleDTO> vaccinationSchedulesDTOPage = vaccinationSchedules.map(vaccinationScheduleMapper::toVaccinationScheduleDTO);
        log.info("vaccinationSchedulesDTOPage in getAllVaccinationSchedule: {}", vaccinationSchedulesDTOPage.getContent());
        return vaccinationSchedulesDTOPage;
    }

    // Lấy Schedule theo id
    public VaccinationScheduleDTO getVaccinationScheduleById(Long id) {
        VaccinationSchedule vaccinationSchedule = vaccinationScheduleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_CHECK_SCHEDULE_NOT_EXISTS));
        return vaccinationScheduleMapper.toVaccinationScheduleDTO(vaccinationSchedule);
    }
}
