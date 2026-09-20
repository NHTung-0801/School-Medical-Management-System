package com.medical.schoolMedical.modules.healthcheck.dto;

import com.medical.schoolMedical.dto.StudentDTO;
import com.medical.schoolMedical.entities.Parent;
import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class HealthCheckConsentDTO {
    private long id;
    @ToString.Exclude
    private StudentDTO student;
    @ToString.Exclude
    private Parent parent;
    @ToString.Exclude
    private HealthCheckSchedule schedule;
    private Long healthCheckRecordId;
    private ConsentStatus status;
    private boolean checkedHealth = false;
    private Boolean sentToParent;
    private boolean needsConsultation;
}
