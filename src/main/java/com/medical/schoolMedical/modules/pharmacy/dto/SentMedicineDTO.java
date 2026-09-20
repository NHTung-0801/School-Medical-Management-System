package com.medical.schoolMedical.modules.pharmacy.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SentMedicineDTO {
    private Long studentId;
    private String medicineList;
    private String usageInstructions;
}
