package com.medical.schoolMedical.modules.healthcheck.services;

import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckRecord;
import com.medical.schoolMedical.modules.healthcheck.repositories.HealthCheckRecordRepository;
import com.medical.schoolMedical.modules.user_management.entities.Student;
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
public class HealthCheckExportService {

    private final HealthCheckRecordRepository healthCheckRecordRepository;

    @Transactional(readOnly = true)
    public void exportToExcel(Long scheduleId, OutputStream outputStream) throws IOException {
        List<HealthCheckRecord> records = healthCheckRecordRepository.findByScheduleId(scheduleId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ket_Qua_Kham_Suc_Khoe");

            // Header Font & Style
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
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
                    "Thị Lực", "Thính Giác", "Huyết Áp", "Nhịp Tim (lần/ph)",
                    "Chiều Cao (cm)", "Cân Nặng (kg)", "Đánh Giá", "Cần Tư Vấn", "Y Tá Thực Hiện"
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
            for (HealthCheckRecord record : records) {
                Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(22);

                Student student = record.getHealthCheckConsent() != null ? record.getHealthCheckConsent().getStudent() : null;

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

                // Thị lực
                Cell c5 = row.createCell(5);
                c5.setCellValue(record.getVisionResult() + "/10");
                c5.setCellStyle(centerCellStyle);

                // Thính giác
                Cell c6 = row.createCell(6);
                c6.setCellValue(record.getHearingResult() != null ? record.getHearingResult() : "Bình thường");
                c6.setCellStyle(centerCellStyle);

                // Huyết áp
                Cell c7 = row.createCell(7);
                c7.setCellValue(record.getBloodPressure() != null ? record.getBloodPressure() : "-");
                c7.setCellStyle(centerCellStyle);

                // Nhịp tim
                Cell c8 = row.createCell(8);
                c8.setCellValue(record.getHeartRate() > 0 ? String.valueOf(record.getHeartRate()) : "-");
                c8.setCellStyle(centerCellStyle);

                // Chiều cao
                Cell c9 = row.createCell(9);
                c9.setCellValue(record.getHeight() > 0 ? String.valueOf(record.getHeight()) : "-");
                c9.setCellStyle(centerCellStyle);

                // Cân nặng
                Cell c10 = row.createCell(10);
                c10.setCellValue(record.getWeight() > 0 ? String.valueOf(record.getWeight()) : "-");
                c10.setCellStyle(centerCellStyle);

                // Đánh giá
                Cell c11 = row.createCell(11);
                c11.setCellValue(record.getAssessment() != null ? record.getAssessment() : "Bình thường");
                c11.setCellStyle(dataCellStyle);

                // Cần tư vấn
                Cell c12 = row.createCell(12);
                c12.setCellValue(record.isNeedsConsultation() ? "CÓ" : "Không");
                c12.setCellStyle(centerCellStyle);

                // Y tá thực hiện
                Cell c13 = row.createCell(13);
                c13.setCellValue(record.getSchoolNurse() != null ? record.getSchoolNurse().getFullName() : "-");
                c13.setCellStyle(dataCellStyle);
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }
}
