package com.medical.schoolMedical.modules.health_record.services;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.health_record.entities.HealthRecord;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HealthRecordPdfExportServiceTest {

    private HealthRecordPdfExportService pdfExportService;
    private HealthRecord sampleRecord;

    @BeforeEach
    void setUp() {
        pdfExportService = new HealthRecordPdfExportService();

        User parentUser = new User();
        parentUser.setUsername("0912345678");

        Parent parent = new Parent();
        parent.setId(1L);
        parent.setFullName("Nguyễn Văn Hùng");
        parent.setPhoneNumber("0912345678");
        parent.setUser(parentUser);

        Student student = new Student();
        student.setId(101L);
        student.setFullName("Nguyễn Văn An");
        student.setClassName("10A1");
        student.setGender(Gender.MALE);
        student.setBirthDate(LocalDate.of(2010, 5, 20));
        student.setAddress("Số 123 Đường Cầu Giấy, Hà Nội");
        student.setParent(parent);

        sampleRecord = new HealthRecord();
        sampleRecord.setId(1L);
        sampleRecord.setStudent(student);
        sampleRecord.setParent(parent);
        sampleRecord.setAllergies("Dị ứng kháng sinh Penicillin, phấn hoa");
        sampleRecord.setChronicDisease("Viêm xoang dị ứng");
        sampleRecord.setTreatmentHistory("Điều trị ngoại trú năm 2024");
        sampleRecord.setVision(10);
        sampleRecord.setHearing("Bình thường");
        sampleRecord.setVaccination("Đã tiêm chủng đầy đủ: Bạch hầu, Uốn ván, Sởi");
        sampleRecord.setOther_health_info("Cần ngồi bàn đầu do cận thị nhẹ");
    }

    @Test
    @DisplayName("Exporting health record to PDF should generate a valid PDF byte stream with %PDF header")
    void testExportToPdf_Success() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfExportService.exportToPdf(sampleRecord, outputStream);

        byte[] pdfBytes = outputStream.toByteArray();
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 500, "PDF byte stream should be substantial");

        // Check PDF Magic header (%PDF)
        String header = new String(pdfBytes, 0, 4, StandardCharsets.US_ASCII);
        assertEquals("%PDF", header, "File should start with standard %PDF magic bytes");
    }
}
