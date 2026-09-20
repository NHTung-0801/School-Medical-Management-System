package com.medical.schoolMedical.modules.pharmacy.repositories;

import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SentMedicineRepository extends JpaRepository<SentMedicine, Long> {
    List<SentMedicine> findByParent_User_Id(Long userId);
}
