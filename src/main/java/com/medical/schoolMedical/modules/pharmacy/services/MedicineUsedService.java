package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.modules.pharmacy.dto.MedicineUsedDTO;
import com.medical.schoolMedical.entities.MedicalEvent;
import com.medical.schoolMedical.modules.pharmacy.entities.Medicine;
import com.medical.schoolMedical.modules.pharmacy.entities.MedicineUsed;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.pharmacy.mappers.MedicineUsedMapper;
import com.medical.schoolMedical.repositories.MedicalEventRepository;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineRepository;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineUsedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineUsedService {
    private final MedicineRepository medicineRepository;
    private final MedicalEventRepository medicalEventRepository;
    private final MedicineUsedRepository medicineUsedRepository;

    public void useMedicine(Long eventId, Long medicineId, int quantity, String note) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICINE_NOT_FOUND));

        if (medicine.getQuantityInStock() < quantity) {
            throw new RuntimeException("Không đủ thuốc");
        }

        MedicalEvent event = medicalEventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDICAL_EVENT_NOT_FOUND));

        // Trừ thuốc
        medicine.setQuantityInStock(medicine.getQuantityInStock() - quantity);
        medicineRepository.save(medicine);

        // Ghi nhận thuốc đã dùng
        MedicineUsed used = new MedicineUsed();
        used.setMedicine(medicine);
        used.setMedicalEvent(event);
        used.setQuantity(quantity);
        used.setNotes(note);

        medicineUsedRepository.save(used);
    }

    public List<MedicineUsedDTO> getUsedMedicinesByEvent(Long eventId) {
        List<MedicineUsed> usedList = medicineUsedRepository.findByMedicalEventId(eventId);

        return usedList.stream()
                .map(MedicineUsedMapper::toDTO)
                .collect(Collectors.toList());
    }
}
