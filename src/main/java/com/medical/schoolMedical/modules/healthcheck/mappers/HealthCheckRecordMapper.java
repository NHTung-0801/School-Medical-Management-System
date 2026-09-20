package com.medical.schoolMedical.modules.healthcheck.mappers;

import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckRecordDTO;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {
        HealthCheckConsentMapper.class,
})
public interface HealthCheckRecordMapper {
    @Mappings({
            @Mapping(source = "healthCheckConsent", target = "healthCheckConsentDTO"),
            @Mapping(source = "schoolNurse", target = "schoolNurseDTO"),
            @Mapping(source = "viewedByParent", target = "viewedByParent")
    })
    HealthCheckRecordDTO toHealthCheckRecordDTO(HealthCheckRecord healthCheckRecord);

    @Mappings({
            @Mapping(source = "healthCheckConsentDTO", target = "healthCheckConsent"),
            @Mapping(source = "schoolNurseDTO", target = "schoolNurse")
    })
    HealthCheckRecord toHealthCheckRecord(HealthCheckRecordDTO healthCheckRecordDTO);

    void updateHealthCheckRecord(@MappingTarget HealthCheckRecord healthCheckRecord, HealthCheckRecord healthCheckRecord_request);
}
