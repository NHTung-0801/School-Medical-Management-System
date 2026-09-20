package com.medical.schoolMedical.modules.vaccination.mappers;

import com.medical.schoolMedical.modules.vaccination.dto.VaccinationScheduleDTO;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationSchedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VaccinationScheduleMapper {
    VaccinationSchedule toVaccinationSchedule(VaccinationScheduleDTO vaccinationScheduleDTO);
    VaccinationScheduleDTO toVaccinationScheduleDTO(VaccinationSchedule vaccinationSchedule);
}
