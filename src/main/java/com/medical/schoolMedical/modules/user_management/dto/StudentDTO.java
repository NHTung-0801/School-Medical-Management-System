package com.medical.schoolMedical.modules.user_management.dto;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StudentDTO {
    private Long id;
    private long healthCheck_recordId;
    @ToString.Exclude
    private Parent parent;

    @NotBlank(message = "Họ và tên học sinh không được để trống")
    private String fullName;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Gender gender;

    @NotNull(message = "Vui lòng chọn ngày sinh")
    private LocalDate birthDate;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Vui lòng nhập tên lớp")
    private String className;

    private LocalDate createAt;
}
