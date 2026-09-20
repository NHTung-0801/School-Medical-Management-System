package com.medical.schoolMedical.modules.user_management.mappers;

import com.medical.schoolMedical.modules.user_management.dto.StudentDTO;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentMapper {
    List<StudentDTO> toStudentDTOs(List<Student> students);
    StudentDTO toStudentDTO(Student student);
}
