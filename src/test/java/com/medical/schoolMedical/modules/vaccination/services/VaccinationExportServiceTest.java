package com.medical.schoolMedical.modules.vaccination.services;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationConsent;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationRecord;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationSchedule;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationRecordRepository;
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
class VaccinationExportServiceTest {

    @Mock
    private VaccinationRecordRepository vaccinationRecordRepository;

    @InjectMocks
    private VaccinationExportService vaccinationExportService;

    private VaccinationRecord sampleRecord;

    @BeforeEach
    void setUp() {
        Student student = new Student();
        student.setId(201L);
        student.setFullName("Trần Thị Bình");
        student.setClassName("11B2");
        student.setGender(Gender.FEMALE);

        VaccinationSchedule schedule = new VaccinationSchedule();
        schedule.setId(10L);
        schedule.setVaccineType("Vắc-xin Uốn ván (Td)");

        VaccinationConsent consent = new VaccinationConsent();
        consent.setStudent(student);
        consent.setSchedule(schedule);

        SchoolNurse nurse = new SchoolNurse();
        nurse.setFullName("Y tá Hương");

        sampleRecord = new VaccinationRecord();
        sampleRecord.setId(1L);
        sampleRecord.setVaccinationConsent(consent);
        sampleRecord.setPostVaccinationCondition("Bình thường, không sốt");
        sampleRecord.setNotes("Theo dõi tại chỗ 30 phút");
        sampleRecord.setSchoolNurse(nurse);
    }

    @Test
    void testExportToExcel_Success() throws IOException {
        when(vaccinationRecordRepository.findByScheduleId(10L)).thenReturn(List.of(sampleRecord));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        vaccinationExportService.exportToExcel(10L, outputStream);

        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0, "Excel output should not be empty");

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Ket_Qua_Tiem_Chung");
            assertNotNull(sheet, "Sheet 'Ket_Qua_Tiem_Chung' should exist");

            // Header row
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow);
            assertEquals("STT", headerRow.getCell(0).getStringCellValue());
            assertEquals("Họ và Tên", headerRow.getCell(2).getStringCellValue());
            assertEquals("Loại Vaccine", headerRow.getCell(5).getStringCellValue());

            // Data row
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow);
            assertEquals("201", dataRow.getCell(1).getStringCellValue());
            assertEquals("Trần Thị Bình", dataRow.getCell(2).getStringCellValue());
            assertEquals("11B2", dataRow.getCell(3).getStringCellValue());
            assertEquals("FEMALE", dataRow.getCell(4).getStringCellValue());
            assertEquals("Vắc-xin Uốn ván (Td)", dataRow.getCell(5).getStringCellValue());
            assertEquals("Bình thường, không sốt", dataRow.getCell(6).getStringCellValue());
            assertEquals("Theo dõi tại chỗ 30 phút", dataRow.getCell(7).getStringCellValue());
            assertEquals("Y tá Hương", dataRow.getCell(8).getStringCellValue());
        }

        verify(vaccinationRecordRepository, times(1)).findByScheduleId(10L);
    }

    @Test
    void testExportToExcel_EmptyRecords() throws IOException {
        when(vaccinationRecordRepository.findByScheduleId(99L)).thenReturn(Collections.emptyList());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        vaccinationExportService.exportToExcel(99L, outputStream);

        byte[] bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Sheet sheet = workbook.getSheet("Ket_Qua_Tiem_Chung");
            assertNotNull(sheet);
            assertNotNull(sheet.getRow(0)); // Headers exist
            assertNull(sheet.getRow(1)); // No data rows
        }
    }
}
