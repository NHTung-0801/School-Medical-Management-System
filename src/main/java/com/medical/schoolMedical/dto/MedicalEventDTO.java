package com.medical.schoolMedical.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class MedicalEventDTO {
    private Long id; // Dùng cho list + xem chi tiết

    @NotNull(message = "Vui lòng chọn học sinh")
    private Long studentId;
    private String studentFullName; // Dùng cho list
    private String nurseFullName;

    private Long nurseId;

    @NotBlank(message = "Địa điểm không được để trống")
    private String location;

    @NotBlank(message = "Mô tả sự việc không được để trống")
    private String description;
    private String initialTreatment;
    private String finalTreatment;
    private String notes;

    private Timestamp eventTime; // Dùng cho list

    private List<MedicineUsedDTO> medicinesUsed = new ArrayList<>();
    private List<SupplyUsedDTO> suppliesUsed = new ArrayList<>();

}
