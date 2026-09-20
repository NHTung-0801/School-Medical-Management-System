package com.medical.schoolMedical.modules.healthcheck.mappers;

import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckConsentDTO;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HealthCheckConsentMapper {
    HealthCheckConsent toEntity(HealthCheckConsentDTO healthCheckConsentDTO);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "student", target = "student"),
            @Mapping(source = "parent", target = "parent"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "healthCheckRecord.sentToParent", target = "sentToParent"),
            @Mapping(source = "healthCheckRecord.needsConsultation", target = "needsConsultation")
    })
    HealthCheckConsentDTO toDTO(HealthCheckConsent healthCheckConsent);

    List<HealthCheckConsentDTO> toHealthCheckConsentDTOs(List<HealthCheckConsent> healthCheckConsents);
}
