package com.medical.schoolMedical.modules.health_record.controllers;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.health_record.entities.HealthRecord;
import com.medical.schoolMedical.modules.health_record.services.HealthRecordService;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NurseHealthRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HealthRecordService healthRecordService;

    private HealthRecord createSampleRecord(Long id, String studentName) {
        Parent parent = new Parent();
        parent.setId(10L);
        parent.setFullName("Phụ huynh của " + studentName);

        Student student = new Student();
        student.setId(100L + id);
        student.setFullName(studentName);
        student.setClassName("10A1");
        student.setGender(Gender.MALE);
        student.setBirthDate(LocalDate.of(2010, 1, 1));
        student.setParent(parent);

        HealthRecord record = new HealthRecord();
        record.setId(id);
        record.setStudent(student);
        record.setParent(parent);
        record.setAllergies("Không");
        record.setChronicDisease("Không");
        record.setTreatmentHistory("Không");
        record.setVision(10);
        record.setHearing("Bình thường");
        record.setVaccination("Đầy đủ");
        return record;
    }

    @Test
    @WithMockUser(username = "nurseUser", roles = {"NURSE"})
    @DisplayName("GET /nurse/health-record without keyword should return all health records")
    void testListHealthRecord_NoKeyword() throws Exception {
        HealthRecord record = createSampleRecord(1L, "Nguyễn Văn A");

        when(healthRecordService.getAll()).thenReturn(List.of(record));

        mockMvc.perform(get("/nurse/health-record"))
                .andExpect(status().isOk())
                .andExpect(view().name("nurse/health-records/health_record_list"))
                .andExpect(model().attributeExists("records"));

        verify(healthRecordService, times(1)).getAll();
    }

    @Test
    @WithMockUser(username = "nurseUser", roles = {"NURSE"})
    @DisplayName("GET /nurse/health-record with keyword should search by student name")
    void testListHealthRecord_WithKeyword() throws Exception {
        HealthRecord record = createSampleRecord(2L, "Lê Minh");

        when(healthRecordService.searchByStudentName("Minh")).thenReturn(List.of(record));

        mockMvc.perform(get("/nurse/health-record").param("keyword", "Minh"))
                .andExpect(status().isOk())
                .andExpect(view().name("nurse/health-records/health_record_list"))
                .andExpect(model().attribute("keyword", "Minh"))
                .andExpect(model().attributeExists("records"));

        verify(healthRecordService, times(1)).searchByStudentName("Minh");
    }

    @Test
    @WithMockUser(username = "nurseUser", roles = {"NURSE"})
    @DisplayName("GET /nurse/health-record/view/{id} with existing record should show detail view")
    void testViewDetail_Found() throws Exception {
        HealthRecord record = createSampleRecord(1L, "Lê Minh");

        when(healthRecordService.findByIdWithStudentAndParent(1L)).thenReturn(Optional.of(record));

        mockMvc.perform(get("/nurse/health-record/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("nurse/health-records/health_record_view"))
                .andExpect(model().attributeExists("record"));
    }

    @Test
    @WithMockUser(username = "nurseUser", roles = {"NURSE"})
    @DisplayName("GET /nurse/health-record/view/{id} when record not found should redirect to list")
    void testViewDetail_NotFound() throws Exception {
        when(healthRecordService.findByIdWithStudentAndParent(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/nurse/health-record/view/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/nurse/health-record"));
    }
}
