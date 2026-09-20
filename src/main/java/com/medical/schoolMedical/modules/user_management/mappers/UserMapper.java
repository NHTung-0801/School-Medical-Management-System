package com.medical.schoolMedical.modules.user_management.mappers;

import com.medical.schoolMedical.modules.user_management.dto.UserDTO;
import com.medical.schoolMedical.modules.user_management.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserDTO userDTO);
}
