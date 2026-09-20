package com.medical.schoolMedical.modules.consultation.mappers;

import com.medical.schoolMedical.modules.consultation.dto.ConsultationAppointmentDTO;
import com.medical.schoolMedical.modules.consultation.entities.ConsultationAppointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConsultationAppointmentMapper {
    ConsultationAppointment toConsultationAppointment(ConsultationAppointmentDTO consultationAppointmentDTO);

    @Mapping(target = "studentDTO", source = "student")
    @Mapping(target = "schoolNurseDTO", source = "schoolNurse")
    ConsultationAppointmentDTO toConsultationAppointmentDTO(ConsultationAppointment consultationAppointment);
}
