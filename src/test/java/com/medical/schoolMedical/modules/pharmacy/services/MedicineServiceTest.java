package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.pharmacy.entities.Medicine;
import com.medical.schoolMedical.modules.pharmacy.repositories.MedicineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineService medicineService;

    @Test
    @DisplayName("Should retrieve all medicines")
    void testGetAllMedicines() {
        Medicine m1 = new Medicine();
        m1.setId(1L);
        m1.setName("Paracetamol");

        when(medicineRepository.findAll()).thenReturn(List.of(m1));

        List<Medicine> list = medicineService.getAllMedicines();

        assertEquals(1, list.size());
        assertEquals("Paracetamol", list.get(0).getName());
    }

    @Test
    @DisplayName("Should get medicine by ID when found")
    void testGetMedicineByIdSuccess() {
        Medicine m = new Medicine();
        m.setId(2L);
        m.setName("Berberin");

        when(medicineRepository.findById(2L)).thenReturn(Optional.of(m));

        Medicine result = medicineService.getMedicineById(2L);

        assertNotNull(result);
        assertEquals("Berberin", result.getName());
    }

    @Test
    @DisplayName("Should throw BusinessException when medicine ID not found")
    void testGetMedicineByIdNotFound() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            medicineService.getMedicineById(99L);
        });

        assertEquals(ErrorCode.MEDICINE_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("Should update medicine quantity and ensure stock never drops below zero")
    void testUpdateQuantityNotBelowZero() {
        Medicine m = new Medicine();
        m.setId(1L);
        m.setQuantityInStock(10);

        when(medicineRepository.findById(1L)).thenReturn(Optional.of(m));

        // Deduct 15, should clamp to 0
        medicineService.updateQuantity(1L, -15);

        assertEquals(0, m.getQuantityInStock());
        verify(medicineRepository, times(1)).save(m);
    }

    @Test
    @DisplayName("Should delete medicine by ID")
    void testDeleteMedicine() {
        medicineService.deleteMedicine(5L);
        verify(medicineRepository, times(1)).deleteById(5L);
    }
}
