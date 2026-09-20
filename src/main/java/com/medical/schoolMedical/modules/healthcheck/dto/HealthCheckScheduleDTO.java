package com.medical.schoolMedical.modules.healthcheck.dto;

import com.medical.schoolMedical.entities.SchoolNurse;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class HealthCheckScheduleDTO {
    private long id;

    @ToString.Exclude
    private SchoolNurse nurse;

    @ToString.Exclude
    private List<HealthCheckConsent> healthCheckConsent;
    private String content;
    private int className;
    private LocalDateTime checkDate;
    private LocalDate sentDate;
    private String notes;

    private boolean sentToParent;

    // ngày và giờ để ghép lại thành LocalDateTime checkDate
    private String date;
    private String time;
}
