package com.medical.schoolMedical.modules.pharmacy.dto;

import lombok.Data;

@Data
public class SupplyUsedDTO {
    private Long supplyId;
    private String supplyName;
    private int quantity;
    private String notes;
}
