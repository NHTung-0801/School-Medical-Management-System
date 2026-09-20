package com.medical.schoolMedical.modules.pharmacy.mappers;

import com.medical.schoolMedical.modules.pharmacy.dto.MedicineUsedDTO;
import com.medical.schoolMedical.modules.pharmacy.entities.MedicineUsed;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicineUsedMapper {
    public static MedicineUsedDTO toDTO(MedicineUsed entity) {
        MedicineUsedDTO dto = new MedicineUsedDTO();
        dto.setMedicineId(entity.getMedicine().getId());
        dto.setMedicineName(entity.getMedicine().getName());
        dto.setQuantity(entity.getQuantity());
        dto.setNotes(entity.getNotes());
        return dto;
    }

    public static MedicineUsed toEntity(MedicineUsedDTO dto) {
        MedicineUsed entity = new MedicineUsed();
        entity.setQuantity(dto.getQuantity());
        entity.setNotes(dto.getNotes());
        return entity;
    }
}
