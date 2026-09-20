package com.medical.schoolMedical.modules.vaccination.mappers;

import com.medical.schoolMedical.modules.vaccination.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationConsent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VaccinationConsentMapper {
    @Mapping(source = "vaccinationRecord.sentToParent", target = "sentToParent")
    VaccinationConsentDTO toVaccinationConsentDTO(VaccinationConsent vaccinationConsent);
    VaccinationConsent toVaccinationConsent(VaccinationConsentDTO vaccinationConsentDTO);
}
