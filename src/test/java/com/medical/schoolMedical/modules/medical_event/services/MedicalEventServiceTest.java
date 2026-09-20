package com.medical.schoolMedical.modules.medical_event.services;

import com.medical.schoolMedical.modules.medical_event.entities.MedicalEvent;
import com.medical.schoolMedical.modules.medical_event.repositories.MedicalEventRepository;
import com.medical.schoolMedical.modules.pharmacy.entities.MedicalSupply;
import com.medical.schoolMedical.modules.pharmacy.entities.Medicine;
import com.medical.schoolMedical.modules.pharmacy.entities.MedicineUsed;
import com.medical.schoolMedical.modules.pharmacy.entities.SupplyUsed;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicalSupplyRepository;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineRepository;
import com.medical.schoolMedical.modules.user_management.repositories.StudentRepository;
import com.medical.schoolMedical.modules.user_management.repositories.UserRepository;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalEventServiceTest {

    @Mock
    private MedicalEventRepository medicalEventRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private MedicalSupplyRepository medicalSupplyRepository;

    @InjectMocks
    private MedicalEventService medicalEventService;

    @Test
    @DisplayName("Should save medical event and deduct medicine & supply stock")
    void testSaveMedicalEventSuccess() {
        // Prepare medicine in stock
        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Oresol");
        medicine.setQuantityInStock(50);

        // Prepare supply in stock
        MedicalSupply supply = new MedicalSupply();
        supply.setId(2L);
        supply.setName("Bang gac vo trung");
        supply.setQuantityInStock(30);

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));
        when(medicalSupplyRepository.findById(2L)).thenReturn(Optional.of(supply));

        // Prepare event with 2 medicine used and 1 supply used
        MedicalEvent event = new MedicalEvent();
        MedicineUsed medUsed = new MedicineUsed();
        medUsed.setMedicine(medicine);
        medUsed.setQuantity(2);

        SupplyUsed supUsed = new SupplyUsed();
        supUsed.setMedicalSupply(supply);
        supUsed.setQuantity(1);

        event.setMedicineUsed(Set.of(medUsed));
        event.setSupplyUsed(Set.of(supUsed));

        medicalEventService.saveMedicalEvent(event);

        // Verify stock deducted
        assertEquals(48, medicine.getQuantityInStock());
        assertEquals(29, supply.getQuantityInStock());

        verify(medicineRepository, times(1)).save(medicine);
        verify(medicalSupplyRepository, times(1)).save(supply);
        verify(medicalEventRepository, times(1)).save(event);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when used medicine quantity exceeds available stock")
    void testSaveMedicalEventExceedsStock() {
        Medicine medicine = new Medicine();
        medicine.setId(1L);
        medicine.setName("Paracetamol");
        medicine.setQuantityInStock(5);

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        MedicalEvent event = new MedicalEvent();
        MedicineUsed medUsed = new MedicineUsed();
        medUsed.setMedicine(medicine);
        medUsed.setQuantity(10); // 10 > 5
        event.setMedicineUsed(Set.of(medUsed));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            medicalEventService.saveMedicalEvent(event);
        });

        assertTrue(ex.getMessage().contains("vượt quá tồn kho"));
        verify(medicalEventRepository, never()).save(any());
    }
}
