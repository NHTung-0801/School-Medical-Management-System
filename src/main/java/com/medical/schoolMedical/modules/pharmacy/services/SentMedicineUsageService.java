package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicineUsage;
import com.medical.schoolMedical.modules.pharmacy.repositories.SentMedicineUsageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SentMedicineUsageService {
    @Autowired
    private SentMedicineUsageRepository sentMedicineUsageRepository;

    public Optional<SentMedicineUsage> getBy_Id(Long id) {
        return sentMedicineUsageRepository.findById(id);
    }

    public SentMedicineUsage create(SentMedicineUsage usage) {
        return sentMedicineUsageRepository.save(usage);
    }

    public List<SentMedicineUsage> getBySentMedicineId(Long id) {
        return sentMedicineUsageRepository.findBySentMedicine_Id(id);
    }

    public List<SentMedicineUsage> getAll() {
        return sentMedicineUsageRepository.findAll();
    }
}
