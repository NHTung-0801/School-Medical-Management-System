package com.medical.schoolMedical.modules.vaccination.dto;

import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationSchedule;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class VaccinationConsentDTO {
    private long id;
    @ToString.Exclude
    private Student student;
    @ToString.Exclude
    private Parent parent;
    @ToString.Exclude
    private VaccinationSchedule schedule;
    private ConsentStatus status;
    private boolean vaccinated = false;
    private Boolean sentToParent;
}
