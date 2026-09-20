package com.medical.schoolMedical.modules.user_management.dto;

import com.medical.schoolMedical.modules.user_management.entities.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class SchoolNurseDTO {
    private Long id;
    @ToString.Exclude
    private User user;
    private String fullName;
    private int experience;
}
