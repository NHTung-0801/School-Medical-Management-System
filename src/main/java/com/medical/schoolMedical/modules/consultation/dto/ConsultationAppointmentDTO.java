package com.medical.schoolMedical.modules.consultation.dto;

import com.medical.schoolMedical.dto.SchoolNurseDTO;
import com.medical.schoolMedical.dto.StudentDTO;
import com.medical.schoolMedical.enums.ConsentStatus;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ConsultationAppointmentDTO {
    private Long id;
    private StudentDTO studentDTO;
    // dùng cái này nhận id cho nhanh
    private Long studentId;
    private SchoolNurseDTO schoolNurseDTO;
    @FutureOrPresent(message = "Ngày tiêm phải là thời điểm ở hiện tại hoặc tương lai")
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private LocalDate sentDate;
    private ConsentStatus status;
    private String content;
}
