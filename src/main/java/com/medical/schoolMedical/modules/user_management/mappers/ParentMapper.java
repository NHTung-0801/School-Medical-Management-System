package com.medical.schoolMedical.modules.user_management.mappers;

import com.medical.schoolMedical.modules.user_management.dto.ParentDTO;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ParentMapper {
    ParentDTO toParentDTO(Parent parent);
}
