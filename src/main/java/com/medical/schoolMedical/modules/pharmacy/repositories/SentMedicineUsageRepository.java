package com.medical.schoolMedical.modules.pharmacy.repositories;

import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicineUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentMedicineUsageRepository extends JpaRepository<SentMedicineUsage, Long> {
    List<SentMedicineUsage> findBySentMedicine_Id(Long sentMedicineId);
}
