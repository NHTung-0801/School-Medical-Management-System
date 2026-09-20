package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.modules.pharmacy.entities.Medicine;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicineService {
    @Autowired
    private MedicineRepository medicineRepository;

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_NOT_FOUND));
    }

    public void saveMedicine(Medicine medicine) {
        medicineRepository.save(medicine);
    }

    public void deleteMedicine(Long id) {
        medicineRepository.deleteById(id);
    }

    public void updateQuantity(Long id, int delta) {
        Medicine medicine = medicineRepository.findById(id).orElse(null);
        if (medicine != null) {
            int newQty = medicine.getQuantityInStock() + delta;
            medicine.setQuantityInStock(Math.max(newQty, 0)); // Không cho âm
            medicineRepository.save(medicine);
        }
    }

    public boolean existsByName(String name) {
        return medicineRepository.findByNameIgnoreCase(name).isPresent();
    }

    public boolean isNameTakenByOtherId(String name, Long id) {
        return medicineRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), id);
    }
}
