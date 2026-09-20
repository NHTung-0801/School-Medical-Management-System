package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.modules.pharmacy.entities.MedicalSupply;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicalSupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalSupplyService {

    @Autowired
    private MedicalSupplyRepository medicalSupplyRepository;

    public List<MedicalSupply> getAllSupplies() {
        return medicalSupplyRepository.findAll();
    }

    public MedicalSupply getSupplyById(Long id) {
        return medicalSupplyRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICAL_SUPPLY_NOT_FOUND));
    }

    public List<MedicalSupply> getAllMedicalSupplies() {
        return medicalSupplyRepository.findAll();
    }

    public void save(MedicalSupply supply) {
        medicalSupplyRepository.save(supply);
    }

    public void deleteById(Long id) {
        medicalSupplyRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return medicalSupplyRepository.findByNameIgnoreCase(name.trim()).isPresent();
    }

    public boolean isNameTakenByOtherId(String name, Long id) {
        return medicalSupplyRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), id);
    }
}
