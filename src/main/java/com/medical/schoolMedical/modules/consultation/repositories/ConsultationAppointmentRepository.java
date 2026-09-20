package com.medical.schoolMedical.modules.consultation.repositories;

import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.consultation.entities.ConsultationAppointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationAppointmentRepository extends JpaRepository<ConsultationAppointment, Long> {
    Page<ConsultationAppointment> findByStatus(ConsentStatus status, Pageable pageable);

    @Query("""
        SELECT ca FROM ConsultationAppointment ca
        WHERE ca.student.parent.user.id = :userId
        ORDER BY ca.id DESC
        """)
    Page<ConsultationAppointment> findAppointmentsByParentUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT ca FROM ConsultationAppointment ca
        WHERE ca.status = :status
        AND ca.student.parent.user.id = :userId
        """)
    Page<ConsultationAppointment> findAcceptedAppointmentsByParentUserId(@Param("status") ConsentStatus status, @Param("userId") Long userId, Pageable pageable);

    // List các appointment chưa confirm
    List<ConsultationAppointment> findByStatus(ConsentStatus status);
}
