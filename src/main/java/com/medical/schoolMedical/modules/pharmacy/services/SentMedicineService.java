package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicine;
import com.medical.schoolMedical.modules.pharmacy.repositories.SentMedicineRepository;
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
public class SentMedicineService {
    @Autowired
    private SentMedicineRepository sentMedicineRepository;

    public SentMedicine create(SentMedicine sentMedicine) {
        return sentMedicineRepository.save(sentMedicine);
    }

    public void delete_by_id(Long id) {
        sentMedicineRepository.deleteById(id);
    }

    public List<SentMedicine> getByParentUserId(Long userId) {
        return sentMedicineRepository.findByParent_User_Id(userId);
    }

    public Optional<SentMedicine> getById(Long id) {
        return sentMedicineRepository.findById(id);
    }

    public List<SentMedicine> getAll() {
        return sentMedicineRepository.findAll();
    }
}
