package com.medical.schoolMedical.modules.vaccination.mappers;

import com.medical.schoolMedical.modules.vaccination.dto.VaccinationRecordDTO;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VaccinationRecordMapper {
    VaccinationRecordDTO toVaccinationRecordDTO(VaccinationRecord vaccinationRecord);
    VaccinationRecord toVaccinationRecord(VaccinationRecordDTO vaccinationRecordDTO);

    // update
    void updateVaccinationRecord(@MappingTarget VaccinationRecord vaccinationRecord, VaccinationRecord vaccinationRecord_request);
}
