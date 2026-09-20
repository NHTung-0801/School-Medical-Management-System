package com.medical.schoolMedical.modules.vaccination.dto;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationScheduleDTO {
    private long id;
    @ToString.Exclude
    private SchoolNurse nurse;
    @NotBlank(message = "Loại vắc xin không được để trống.")
    private String vaccineType;
    private String recommendedAgeMonths;
    @NotNull(message = "Ngày tiêm không được để trống.")
    @FutureOrPresent(message = "Ngày tiêm phải là thời điểm ở hiện tại hoặc tương lai")
    private LocalDateTime injectionDate;
    private LocalDate sentDate;
    private String notes;
    private boolean sentToParent;
}
