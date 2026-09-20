package com.medical.schoolMedical.mapper;

import com.medical.schoolMedical.dto.VaccinationRecordDTO;
import com.medical.schoolMedical.entities.VaccinationRecord;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VaccinationRecordMapper {
    VaccinationRecordDTO toVaccinationRecordDTO(VaccinationRecord vaccinationRecord);
    VaccinationRecord toVaccinationRecord(VaccinationRecordDTO vaccinationRecordDTO);

    //    update
    void updateVaccinationRecord(@MappingTarget VaccinationRecord vaccinationRecord, VaccinationRecord vaccinationRecord_request);

}
