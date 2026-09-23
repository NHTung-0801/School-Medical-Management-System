package com.medical.schoolMedical.modules.vaccination.repositories;

import com.medical.schoolMedical.modules.vaccination.entities.VaccinationConsent;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    Optional<VaccinationRecord> findByVaccinationConsent(VaccinationConsent vaccinationConsent);

    @Query("SELECT r FROM VaccinationRecord r WHERE r.vaccinationConsent.id IN :consentIds")
    List<VaccinationRecord> findByConsentIds(@Param("consentIds") List<Long> consentIds);

    boolean existsByVaccinationConsent_Parent_IdAndSentToParentTrueAndViewedByParentFalse(Long parentId);

    Page<VaccinationRecord> findBySentToParentTrueAndVaccinationConsent_Parent_User_Id(Long userId, Pageable pageable);

    @Query("SELECT r FROM VaccinationRecord r JOIN FETCH r.vaccinationConsent c JOIN FETCH c.student WHERE c.schedule.id = :scheduleId")
    List<VaccinationRecord> findByScheduleId(@Param("scheduleId") Long scheduleId);
}
