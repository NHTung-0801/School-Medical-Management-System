package com.medical.schoolMedical.modules.pharmacy.repositories;

import com.medical.schoolMedical.modules.pharmacy.entities.MedicineUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineUsedRepository extends JpaRepository<MedicineUsed, Long> {
    List<MedicineUsed> findByMedicalEventId(Long eventId);
}
