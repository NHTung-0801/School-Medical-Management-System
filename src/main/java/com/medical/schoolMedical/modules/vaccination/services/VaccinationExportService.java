package com.medical.schoolMedical.modules.vaccination.services;

import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.vaccination.entities.VaccinationRecord;
import com.medical.schoolMedical.modules.vaccination.repositories.VaccinationRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaccinationExportService {

    private final VaccinationRecordRepository vaccinationRecordRepository;

    @Transactional(readOnly = true)
    public void exportToExcel(Long scheduleId, OutputStream outputStream) throws IOException {
        List<VaccinationRecord> records = vaccinationRecordRepository.findByScheduleId(scheduleId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ket_Qua_Tiem_Chung");

            // Header Font & Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.TEAL.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);

            // Data Style
            CellStyle dataCellStyle = workbook.createCellStyle();
            dataCellStyle.setBorderTop(BorderStyle.THIN);
            dataCellStyle.setBorderBottom(BorderStyle.THIN);
            dataCellStyle.setBorderLeft(BorderStyle.THIN);
            dataCellStyle.setBorderRight(BorderStyle.THIN);
            dataCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Center Data Style
            CellStyle centerCellStyle = workbook.createCellStyle();
            centerCellStyle.cloneStyleFrom(dataCellStyle);
            centerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Headers
            String[] headers = {
                    "STT", "Mã HS", "Họ và Tên", "Lớp", "Giới Tính",
                    "Loại Vaccine", "Tình Trạng Sau Tiêm", "Ghi Chú", "Y Tá Thực Hiện"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Fill Data Rows
            int rowIdx = 1;
            for (VaccinationRecord record : records) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(22);

                Student student = record.getVaccinationConsent() != null ? record.getVaccinationConsent().getStudent() : null;
                String vaccineType = record.getVaccinationConsent() != null && record.getVaccinationConsent().getSchedule() != null
                        ? record.getVaccinationConsent().getSchedule().getVaccineType()
                        : "-";

                // STT
                Cell c0 = row.createCell(0);
                c0.setCellValue(rowIdx - 1);
                c0.setCellStyle(centerCellStyle);

                // Mã HS
                Cell c1 = row.createCell(1);
                c1.setCellValue(student != null && student.getId() != null ? String.valueOf(student.getId()) : "-");
                c1.setCellStyle(centerCellStyle);

                // Họ và tên
                Cell c2 = row.createCell(2);
                c2.setCellValue(student != null ? student.getFullName() : "-");
                c2.setCellStyle(dataCellStyle);

                // Lớp
                Cell c3 = row.createCell(3);
                c3.setCellValue(student != null ? student.getClassName() : "-");
                c3.setCellStyle(centerCellStyle);

                // Giới tính
                Cell c4 = row.createCell(4);
                c4.setCellValue(student != null && student.getGender() != null ? student.getGender().name() : "-");
                c4.setCellStyle(centerCellStyle);

                // Loại Vaccine
                Cell c5 = row.createCell(5);
                c5.setCellValue(vaccineType);
                c5.setCellStyle(dataCellStyle);

                // Tình trạng sau tiêm
                Cell c6 = row.createCell(6);
                c6.setCellValue(record.getPostVaccinationCondition() != null ? record.getPostVaccinationCondition() : "Bình thường");
                c6.setCellStyle(dataCellStyle);

                // Ghi chú
                Cell c7 = row.createCell(7);
                c7.setCellValue(record.getNotes() != null ? record.getNotes() : "");
                c7.setCellStyle(dataCellStyle);

                // Y tá thực hiện
                Cell c8 = row.createCell(8);
                c8.setCellValue(record.getSchoolNurse() != null ? record.getSchoolNurse().getFullName() : "-");
                c8.setCellStyle(dataCellStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }
}
