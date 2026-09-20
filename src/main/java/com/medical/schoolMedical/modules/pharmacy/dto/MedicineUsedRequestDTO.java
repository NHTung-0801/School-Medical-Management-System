package com.medical.schoolMedical.modules.pharmacy.dto;

import lombok.Data;

@Data
public class MedicineUsedRequestDTO {
    private Long medicineId;
    private int quantity;
    private String notes;
}
