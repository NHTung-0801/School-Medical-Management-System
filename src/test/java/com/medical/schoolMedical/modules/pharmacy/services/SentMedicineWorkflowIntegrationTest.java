package com.medical.schoolMedical.modules.pharmacy.services;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicine;
import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicineUsage;
import com.medical.schoolMedical.modules.pharmacy.repositories.SentMedicineRepository;
import com.medical.schoolMedical.modules.pharmacy.repositories.SentMedicineUsageRepository;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.modules.user_management.entities.Student;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SentMedicineWorkflowIntegrationTest {

    @Mock
    private SentMedicineRepository sentMedicineRepository;

    @Mock
    private SentMedicineUsageRepository sentMedicineUsageRepository;

    @InjectMocks
    private SentMedicineService sentMedicineService;

    @InjectMocks
    private SentMedicineUsageService sentMedicineUsageService;

    @Test
    @DisplayName("End-to-end workflow: Parent creates sent medicine request and Nurse logs medication usage")
    void testSentMedicineWorkflow_Success() {
        // Step 1: Parent submits medicine request
        Student student = new Student();
        student.setId(10L);
        student.setFullName("Nguyễn Minh Khôi");
        student.setClassName("5A1");
        student.setGender(Gender.MALE);

        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFullName("Phụ huynh Nguyễn Văn Cường");

        SentMedicine sentMedicine = new SentMedicine();
        sentMedicine.setId(100L);
        sentMedicine.setStudent(student);
        sentMedicine.setParent(parent);
        sentMedicine.setMedicineList("Paracetamol 250mg, Siro ho Prospan");
        sentMedicine.setUsageInstructions("1 gói hòa nước uống sau ăn trưa, siro uống 5ml");
        sentMedicine.setSentDate(LocalDate.now());

        when(sentMedicineRepository.save(any(SentMedicine.class))).thenReturn(sentMedicine);
        when(sentMedicineRepository.findById(100L)).thenReturn(Optional.of(sentMedicine));

        SentMedicine createdMedicine = sentMedicineService.create(sentMedicine);
        assertNotNull(createdMedicine);
        assertEquals("Paracetamol 250mg, Siro ho Prospan", createdMedicine.getMedicineList());
        assertEquals("1 gói hòa nước uống sau ăn trưa, siro uống 5ml", createdMedicine.getUsageInstructions());

        // Step 2: Nurse retrieves sent medicine and administers dose
        Optional<SentMedicine> nurseRetrieved = sentMedicineService.getById(100L);
        assertTrue(nurseRetrieved.isPresent());

        SchoolNurse nurse = new SchoolNurse();
        nurse.setId(5L);
        nurse.setFullName("Y tá Nguyễn Thị Hoa");

        SentMedicineUsage usage = new SentMedicineUsage();
        usage.setId(1001L);
        usage.setSentMedicine(nurseRetrieved.get());
        usage.setSchoolNurse(nurse);
        usage.setUsageTime(LocalDate.now());
        usage.setMedicineName("Paracetamol 250mg");
        usage.setDosage("1 gói 250mg");
        usage.setNotes("Đã cho học sinh uống lúc 12h15 sau bữa ăn trưa, nhiệt độ hạ còn 37 độ C");

        when(sentMedicineUsageRepository.save(any(SentMedicineUsage.class))).thenReturn(usage);
        when(sentMedicineUsageRepository.findBySentMedicine_Id(100L)).thenReturn(List.of(usage));

        SentMedicineUsage loggedUsage = sentMedicineUsageService.create(usage);
        assertNotNull(loggedUsage);
        assertEquals(1001L, loggedUsage.getId());
        assertEquals(nurse, loggedUsage.getSchoolNurse());
        assertEquals("Paracetamol 250mg", loggedUsage.getMedicineName());

        // Step 3: Verify usage history can be viewed by medicine ID
        List<SentMedicineUsage> history = sentMedicineUsageService.getBySentMedicineId(100L);
        assertEquals(1, history.size());
        assertEquals("Đã cho học sinh uống lúc 12h15 sau bữa ăn trưa, nhiệt độ hạ còn 37 độ C", history.get(0).getNotes());

        verify(sentMedicineRepository, times(1)).save(any(SentMedicine.class));
        verify(sentMedicineUsageRepository, times(1)).save(any(SentMedicineUsage.class));
        verify(sentMedicineUsageRepository, times(1)).findBySentMedicine_Id(100L);
    }
}
