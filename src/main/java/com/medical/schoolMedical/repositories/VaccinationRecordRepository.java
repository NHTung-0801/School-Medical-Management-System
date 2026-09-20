package com.medical.schoolMedical.repositories;

import com.medical.schoolMedical.entities.VaccinationConsent;
import com.medical.schoolMedical.entities.VaccinationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord,Long> {
    Optional<VaccinationRecord> findByVaccinationConsent(VaccinationConsent vaccinationConsent);

    @Query("SELECT r FROM VaccinationRecord r WHERE r.vaccinationConsent.id IN :consentIds")
    List<VaccinationRecord> findByConsentIds(@Param("consentIds") List<Long> consentIds);

    Page<VaccinationRecord> findBySentToParentTrueAndVaccinationConsent_Parent_User_Id(Long userId, Pageable pageable);
}
