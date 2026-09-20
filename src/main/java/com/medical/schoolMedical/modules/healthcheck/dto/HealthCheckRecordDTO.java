package com.medical.schoolMedical.modules.healthcheck.dto;

import com.medical.schoolMedical.dto.SchoolNurseDTO;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HealthCheckRecordDTO {
    private long id;
    private HealthCheckConsentDTO healthCheckConsentDTO;
    private SchoolNurseDTO schoolNurseDTO;
    private long healthCheckConsentId;

    @Min(value = 0, message = "Kết quả thị lực phải lớn hơn hoặc bằng 0.")
    @Max(value = 10, message = "Kết quả thị lực phải nhỏ hơn hoặc bằng 10.")
    private int visionResult;

    @NotBlank(message = "Không được để trống kết quả đo thính giác")
    private String hearingResult;

    private String bloodPressure;
    private int heartRate;
    private double height;
    private double weight;
    private String otherResult;
    private String assessment;
    private boolean needsConsultation = false;
    private boolean is_sent_to_parentv = false;
    private boolean viewedByParent = false;
}
