package com.medical.schoolMedical.repositories;

import com.medical.schoolMedical.entities.VaccinationConsent;
import com.medical.schoolMedical.entities.VaccinationSchedule;
import com.medical.schoolMedical.enums.ConsentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VaccinationConsentRepository extends JpaRepository<VaccinationConsent, Long> {
    //    Lấy các vaccinationConsent của parent tươnhg ưnhgs
    Page<VaccinationConsent> findByParent_User_IdOrderByIdDesc(Long userId, Pageable pageable);

    //    Lấy danh sách consent đồng ý tiêm và trạng thái tiêm hay chưa tiêm tùy ng dùng chọn
    @Query(value = """
    SELECT v FROM VaccinationConsent v
    WHERE v.schedule = :scheduleId
    AND v.status = :status
    AND v.vaccinated = :is_vaccinated
    ORDER BY v.id ASC
    """)
    Page<VaccinationConsent> listConsentsByScheduleAndStatusAndCheckState(
            @Param("scheduleId") VaccinationSchedule scheduleId,
            @Param("status") ConsentStatus status,
            @Param("is_vaccinated") boolean is_vaccinated,
            Pageable pageable
    );

    @Query("SELECT v FROM VaccinationConsent v JOIN FETCH v.schedule WHERE v.status = :status")
    List<VaccinationConsent> findByStatusWithSchedule(@Param("status") ConsentStatus status);
}
