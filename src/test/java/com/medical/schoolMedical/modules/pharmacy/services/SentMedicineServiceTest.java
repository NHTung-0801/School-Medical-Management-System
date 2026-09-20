package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicine;
import com.medical.schoolMedical.modules.pharmacy.repositories.SentMedicineRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SentMedicineServiceTest {

    @Mock
    private SentMedicineRepository sentMedicineRepository;

    @InjectMocks
    private SentMedicineService sentMedicineService;

    @Test
    @DisplayName("Should create sent medicine successfully")
    void testCreateSentMedicine() {
        SentMedicine sentMedicine = new SentMedicine();
        sentMedicine.setId(1L);
        sentMedicine.setMedicineList("Siro ho Prospan");

        when(sentMedicineRepository.save(sentMedicine)).thenReturn(sentMedicine);

        SentMedicine result = sentMedicineService.create(sentMedicine);

        assertNotNull(result);
        assertEquals("Siro ho Prospan", result.getMedicineList());
        verify(sentMedicineRepository, times(1)).save(sentMedicine);
    }

    @Test
    @DisplayName("Should get sent medicines by parent user ID")
    void testGetByParentUserId() {
        SentMedicine sent = new SentMedicine();
        sent.setId(2L);

        when(sentMedicineRepository.findByParent_User_Id(10L)).thenReturn(List.of(sent));

        List<SentMedicine> list = sentMedicineService.getByParentUserId(10L);

        assertEquals(1, list.size());
        assertEquals(2L, list.get(0).getId());
    }

    @Test
    @DisplayName("Should delete sent medicine by ID")
    void testDeleteById() {
        sentMedicineService.delete_by_id(3L);
        verify(sentMedicineRepository, times(1)).deleteById(3L);
    }
}
