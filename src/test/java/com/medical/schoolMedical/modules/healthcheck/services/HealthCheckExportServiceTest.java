package com.medical.schoolMedical.modules.healthcheck.services;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckRecord;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckRecordRepository;
import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckExportServiceTest {

    @Mock
    private HealthCheckRecordRepository healthCheckRecordRepository;

    @InjectMocks
    private HealthCheckExportService healthCheckExportService;

    private HealthCheckRecord sampleRecord;

    @BeforeEach
    void setUp() {
        Student student = new Student();
        student.setId(101L);
        student.setFullName("Nguyễn Văn An");
        student.setClassName("10A1");
        student.setGender(Gender.MALE);

        HealthCheckConsent consent = new HealthCheckConsent();
        consent.setStudent(student);

        SchoolNurse nurse = new SchoolNurse();
        nurse.setFullName("Y tá Lan");

        sampleRecord = new HealthCheckRecord();
        sampleRecord.setId(1L);
        sampleRecord.setHealthCheckConsent(consent);
        sampleRecord.setVisionResult(10);
        sampleRecord.setHearingResult("Bình thường");
        sampleRecord.setBloodPressure("120/80");
        sampleRecord.setHeartRate(75);
        sampleRecord.setHeight(170);
        sampleRecord.setWeight(65);
        sampleRecord.setAssessment("Sức khỏe tốt");
        sampleRecord.setNeedsConsultation(false);
        sampleRecord.setSchoolNurse(nurse);
    }

    @Test
    void testExportToExcel_Success() throws IOException {
        when(healthCheckRecordRepository.findByScheduleId(1L)).thenReturn(List.of(sampleRecord));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        healthCheckExportService.exportToExcel(1L, outputStream);

        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0, "Excel output should not be empty");

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Ket_Qua_Kham_Suc_Khoe");
            assertNotNull(sheet, "Sheet 'Ket_Qua_Kham_Suc_Khoe' should exist");

            // Header row
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("STT", headerRow.getCell(0).getStringCellValue());
            assertEquals("Họ và Tên", headerRow.getCell(2).getStringCellValue());

            // Data row
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            assertEquals("101", dataRow.getCell(1).getStringCellValue());
            assertEquals("Nguyễn Văn An", dataRow.getCell(2).getStringCellValue());
            assertEquals("10A1", dataRow.getCell(3).getStringCellValue());
            assertEquals("MALE", dataRow.getCell(4).getStringCellValue());
            assertEquals("10/10", dataRow.getCell(5).getStringCellValue());
            assertEquals("Sức khỏe tốt", dataRow.getCell(11).getStringCellValue());
            assertEquals("Không", dataRow.getCell(12).getStringCellValue());
            assertEquals("Y tá Lan", dataRow.getCell(13).getStringCellValue());
        }

        verify(healthCheckRecordRepository, times(1)).findByScheduleId(1L);
    }

    @Test
    void testExportToExcel_EmptyRecords() throws IOException {
        when(healthCheckRecordRepository.findByScheduleId(2L)).thenReturn(Collections.emptyList());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        healthCheckExportService.exportToExcel(2L, outputStream);

        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Ket_Qua_Kham_Suc_Khoe");
            assertNotNull(sheet);
            assertNotNull(sheet.getRow(0)); // Headers exist
            assertNull(sheet.getRow(1)); // No data rows
        }
    }
}
