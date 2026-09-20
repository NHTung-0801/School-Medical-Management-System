package com.medical.schoolMedical.service;

import com.medical.schoolMedical.dto.ConsultationAppointmentDTO;
import com.medical.schoolMedical.entities.ConsultationAppointment;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import com.medical.schoolMedical.entities.SchoolNurse;
import com.medical.schoolMedical.entities.Student;
import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.mapper.ConsultationAppointmentMapper;
import com.medical.schoolMedical.repositories.ConsultationAppointmentRepository;
import com.medical.schoolMedical.repositories.SchoolNurseRepository;
import com.medical.schoolMedical.repositories.StudentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConsultationAppointmentService {

    @Autowired
    private ConsultationAppointmentRepository consultationAppointmentRepository;
    @Autowired
    private ConsultationAppointmentMapper consultationAppointmentMapper;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SchoolNurseRepository schoolNurseRepository;

//    vừa tạo vừa gửi đến phụ huynh
    public void createConsAppoint(ConsultationAppointmentDTO appointmentDTO, long userId) {
//        Lấy student và nurse
        Student student = studentRepository.findById(appointmentDTO.getStudentId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_EXISTS));;

       SchoolNurse schoolNurse = schoolNurseRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHOOL_NURSE_NOT_EXISTS));

        // Tạo ConsultationAppointment hoàn thiện
        ConsultationAppointment appointment = createConsAppoint(appointmentDTO, student, schoolNurse);
//        log.info("ConsultationAppointment in createConsAppoint Service : {}", appointment);

        try {
            consultationAppointmentRepository.save(appointment);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_CONSULTATION_APPOINTMENT_FAILED);
        }

    }

    private ConsultationAppointment createConsAppoint(ConsultationAppointmentDTO appointmentDTO,Student student, SchoolNurse schoolNurse) {
        return ConsultationAppointment.builder()
                .student(student)
                .schoolNurse(schoolNurse)
                .status(ConsentStatus.UNCONFIRMED)
                .content(appointmentDTO.getContent())
                .scheduledTime(appointmentDTO.getScheduledTime())
                .sentToParent(true)
                .sentDate(LocalDate.now())
                .build();

    }

//    Danh sách các lịch hẹn tư vấn đã tạo
    public Page<ConsultationAppointmentDTO> getAllConsultationAppointments(int page) {
        int size = 10;
        Page<ConsultationAppointment> appointments = consultationAppointmentRepository.findAll(PageRequest.of(page, size,  Sort.by(Sort.Direction.DESC, "id")));

        //        CHuyển qua Page<ConsultationAppointmentDTO>
        Page<ConsultationAppointmentDTO> appointmentDTOS = appointments.map(consultationAppointmentMapper::toConsultationAppointmentDTO);
//        log.info("ConsultationAppointmentDTO in getAllConsultationAppointments: {}", appointmentDTOS.getContent());
        return appointmentDTOS;
    }

    //    Danh sách các lịch hẹn tư vấn đã được phụ huynh xác nhận, giao diện phía nurse
    public Page<ConsultationAppointmentDTO> getAllAppointmentAccepted(int page) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size,  Sort.by(Sort.Direction.DESC, "id"));
        Page<ConsultationAppointment> appointments = consultationAppointmentRepository.findByStatus(ConsentStatus.ACCEPTED, pageable);

        //        CHuyển qua Page<ConsultationAppointmentDTO>
        Page<ConsultationAppointmentDTO> appointmentDTOS = appointments.map(consultationAppointmentMapper::toConsultationAppointmentDTO);
//        log.info("ConsultationAppointmentDTO in getAllAppointmentAccepted: {}", appointmentDTOS.getContent());
        return appointmentDTOS;
    }

    //    Danh sách các lịch hẹn tư vấn cần thông báo của phía giao diện parent
    public Page<ConsultationAppointmentDTO> getAllAppointment_parent(long userId,int page) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsultationAppointment> appointments = consultationAppointmentRepository.findAppointmentsByParentUserId(userId, pageable);

        //        CHuyển qua Page<ConsultationAppointmentDTO>
        Page<ConsultationAppointmentDTO> appointmentDTOS = appointments.map(consultationAppointmentMapper::toConsultationAppointmentDTO);
        log.info("ConsultationAppointmentDTO in getAllAppointment_parent: {}", appointmentDTOS.getContent());
        return appointmentDTOS;
    }

    //    Danh sách các lịch hẹn tư vấn đã đồng ý của phía giao diện parent
    public Page<ConsultationAppointmentDTO> getAllAppointmentAccepted_Parent(long userId,int page) {
        int size = 10;
        Pageable pageable = PageRequest.of(page, size,  Sort.by(Sort.Direction.DESC, "id"));
        Page<ConsultationAppointment> appointments = consultationAppointmentRepository.findAcceptedAppointmentsByParentUserId(ConsentStatus.ACCEPTED,userId, pageable);

        //        CHuyển qua Page<ConsultationAppointmentDTO>
        Page<ConsultationAppointmentDTO> appointmentDTOS = appointments.map(consultationAppointmentMapper::toConsultationAppointmentDTO);
//        log.info("ConsultationAppointmentDTO in getAllAppointmentAccepted_Parent: {}", appointmentDTOS.getContent());
        return appointmentDTOS;
    }

//    Lấy lịch hẹn tư vấn theo ID
    private ConsultationAppointment getConsultationAppointmentById(long appointmentId) {
        ConsultationAppointment appointment = consultationAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSULTATION_APPOINTMENT_NOT_EXISTS));
        return appointment;
    }

//    Lấy lịch hẹn tư vấn theo id và cập nhật nó là đã xemn từ phía phụ huynh
    public ConsultationAppointmentDTO getAndUpdateViewedByParent_ConsultationAppointment(long appointmentId) {
        ConsultationAppointment appointment = null;
        try{
            appointment = getConsultationAppointmentById(appointmentId);
        }catch (BusinessException e){
            throw new BusinessException(e.getErrorCode());
        }
        appointment.setViewedByParent(true);
        try {
            return consultationAppointmentMapper.toConsultationAppointmentDTO(consultationAppointmentRepository.save(appointment)) ;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SAVE_CONSULTATION_APPOINTMENT_FAILED);
        }
    }

    //    Xử lí trạng thái lịch hẹn tư vấn khi người dùng đồng ý hoặc không
    public void confirmAppointment(Long appointmentId, String response) {
        ConsultationAppointment appointment = null;
        try{
            appointment = getConsultationAppointmentById(appointmentId);
        }catch (BusinessException e){
            throw new BusinessException(e.getErrorCode());
        }

        if(appointment.getScheduledTime().isBefore(LocalDateTime.now()) || "OVERDUE".equals(appointment.getStatus().name())){
            throw new BusinessException(ErrorCode.SURVEY_EXPIRED);
        }
        if("agree".equals(response)){
            appointment.setStatus(ConsentStatus.ACCEPTED);
        }else if("disagree".equals(response)){
            appointment.setStatus(ConsentStatus.DECLINED);
        }
        try{
            consultationAppointmentRepository.save(appointment);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.SAVE_CONSULTATION_APPOINTMENT_FAILED);
        }

    }

    //    Hàm tự động cập nhật trạng thái lịch hẹn hết hạn nếu quá ngày

    public void update_SurveyExpired_Appointment() {
        List<ConsultationAppointment> unconfirms = consultationAppointmentRepository.findByStatus(ConsentStatus.UNCONFIRMED);
        List<ConsultationAppointment> toUpdate = new ArrayList<>();
        for(ConsultationAppointment consultationAppointment : unconfirms){
            if(consultationAppointment.getScheduledTime().isBefore(LocalDateTime.now())){
                consultationAppointment.setStatus(ConsentStatus.OVERDUE);
                toUpdate.add(consultationAppointment);
            }
        }
        consultationAppointmentRepository.saveAll(toUpdate);

    }


}
