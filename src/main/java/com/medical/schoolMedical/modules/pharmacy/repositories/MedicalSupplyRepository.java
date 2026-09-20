package com.medical.schoolMedical.modules.pharmacy.repositories;

import com.medical.schoolMedical.modules.pharmacy.entities.MedicalSupply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalSupplyRepository extends JpaRepository<MedicalSupply, Long> {
    // Tìm vật tư theo tên (không phân biệt hoa thường)
    Optional<MedicalSupply> findByNameIgnoreCase(String name);

    // Kiểm tra xem tên đã tồn tại cho một id khác chưa (dùng khi cập nhật)
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
