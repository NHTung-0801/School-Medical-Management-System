package com.medical.schoolMedical.modules.healthcheck.mappers;

import com.medical.schoolMedical.modules.healthcheck.dto.HealthCheckScheduleDTO;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HealthCheckScheduleMapper {

    HealthCheckScheduleDTO toHealthCheckScheduleDTO(HealthCheckSchedule healthCheckSchedule);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "content", target = "content"),
            @Mapping(source = "className", target = "className"),
            @Mapping(source = "checkDate", target = "checkDate"),
            @Mapping(source = "sentDate", target = "sentDate"),
            @Mapping(source = "notes", target = "notes"),
            @Mapping(source = "healthCheckConsent", target = "healthCheckConsent"),
            @Mapping(source = "nurse", target = "nurse")
    })
    HealthCheckSchedule toHealthCheckSchedule(HealthCheckScheduleDTO healthCheckScheduleDTO);

    List<HealthCheckScheduleDTO> toListHealthCheckScheduleDTO(List<HealthCheckSchedule> healthCheckSchedules);
}
