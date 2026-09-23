package com.medical.schoolMedical.modules.health_record.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.medical.schoolMedical.modules.health_record.entities.HealthRecord;
import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class HealthRecordPdfExportService {

    private static final Color PRIMARY_COLOR = new Color(2, 132, 199); // #0284c7
    private static final Color DARK_HEADER = new Color(15, 23, 42);   // #0f172a
    private static final Color BG_SECTION = new Color(240, 249, 255); // #f0f9ff
    private static final Color BORDER_COLOR = new Color(226, 232, 240); // #e2e8f0

    private BaseFont getBaseFont() {
        // Cố gắng tìm phông Arial/DejaVu có hỗ trợ tiếng Việt UTF-8
        String[] fontPaths = {
                "C:/Windows/Fonts/arial.ttf",
                "C:/Windows/Fonts/times.ttf",
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
                "/usr/share/fonts/TTF/DejaVuSans.ttf"
        };

        for (String path : fontPaths) {
            File fontFile = new File(path);
            if (fontFile.exists()) {
                try {
                    return BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                } catch (Exception e) {
                    log.debug("Could not load font from {}", path, e);
                }
            }
        }

        // Fallback font
        try {
            return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize base font for PDF generation", e);
        }
    }

    public void exportToPdf(HealthRecord record, OutputStream outputStream) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        BaseFont bf = getBaseFont();
        Font titleFont = new Font(bf, 16, Font.BOLD, PRIMARY_COLOR);
        Font subtitleFont = new Font(bf, 10, Font.ITALIC, Color.GRAY);
        Font headerTopFont = new Font(bf, 9, Font.BOLD, DARK_HEADER);
        Font sectionTitleFont = new Font(bf, 11, Font.BOLD, PRIMARY_COLOR);
        Font boldFont = new Font(bf, 10, Font.BOLD, DARK_HEADER);
        Font regularFont = new Font(bf, 10, Font.NORMAL, DARK_HEADER);
        Font noteFont = new Font(bf, 9, Font.ITALIC, Color.DARK_GRAY);

        // 1. Header Table (Quốc hiệu tiêu ngữ & Tên trường)
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{55f, 45f});

        PdfPCell cellLeft = new PdfPCell();
        cellLeft.setBorder(Rectangle.NO_BORDER);
        Paragraph schoolPara = new Paragraph("SỞ GIÁO DỤC VÀ ĐÀO TẠO HÀ NỘI\nTRƯỜNG TIỂU HỌC & THCS Y TẾ HỌC ĐƯỜNG\nBỘ PHẬN Y TẾ HỌC ĐƯỜNG", headerTopFont);
        schoolPara.setAlignment(Element.ALIGN_CENTER);
        cellLeft.addElement(schoolPara);
        headerTable.addCell(cellLeft);

        PdfPCell cellRight = new PdfPCell();
        cellRight.setBorder(Rectangle.NO_BORDER);
        Paragraph nationPara = new Paragraph("CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM\nĐộc lập - Tự do - Hạnh phúc\n-------------------", headerTopFont);
        nationPara.setAlignment(Element.ALIGN_CENTER);
        cellRight.addElement(nationPara);
        headerTable.addCell(cellRight);

        document.add(headerTable);
        document.add(new Paragraph(" "));

        // 2. Main Title
        Paragraph title = new Paragraph("THẺ Y TẾ ĐIỆN TỬ HỌC SINH", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("(SCHOOL ELECTRONIC HEALTH CARD)", subtitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subTitle);
        document.add(new Paragraph(" "));

        Student student = record.getStudent();
        Parent parent = record.getParent() != null ? record.getParent() : (student != null ? student.getParent() : null);

        // 3. Section I: Thông tin học sinh
        addSectionHeader(document, "I. THÔNG TIN HỌC SINH & GIA ĐÌNH", sectionTitleFont);

        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{22f, 28f, 22f, 28f});

        addTableRow(infoTable, "Mã học sinh:", student != null && student.getId() != null ? String.valueOf(student.getId()) : "-", boldFont, regularFont);
        addTableRow(infoTable, "Họ và tên:", student != null ? student.getFullName() : "-", boldFont, regularFont);

        String dob = (student != null && student.getBirthDate() != null)
                ? student.getBirthDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "-";
        String gender = (student != null && student.getGender() != null) ? (student.getGender().name().equals("MALE") ? "Nam" : "Nữ") : "-";
        addTableRow(infoTable, "Ngày sinh:", dob, boldFont, regularFont);
        addTableRow(infoTable, "Giới tính:", gender, boldFont, regularFont);

        addTableRow(infoTable, "Lớp học:", student != null ? student.getClassName() : "-", boldFont, regularFont);
        addTableRow(infoTable, "Phụ huynh:", parent != null ? parent.getFullName() : "-", boldFont, regularFont);

        String phone = (parent != null && parent.getPhoneNumber() != null)
                ? parent.getPhoneNumber()
                : (parent != null && parent.getUser() != null ? parent.getUser().getUsername() : "-");
        addTableRow(infoTable, "SĐT liên hệ:", phone, boldFont, regularFont);
        addTableRow(infoTable, "Địa chỉ:", (student != null && student.getAddress() != null) ? student.getAddress() : "-", boldFont, regularFont);

        document.add(infoTable);
        document.add(new Paragraph(" "));

        // 4. Section II: Tiền sử y tế & Dị ứng
        addSectionHeader(document, "II. TIỀN SỬ Y TẾ & BỆNH LÝ MÃN TÍNH", sectionTitleFont);

        PdfPTable medicalTable = new PdfPTable(2);
        medicalTable.setWidthPercentage(100);
        medicalTable.setWidths(new float[]{30f, 70f});

        addTableRow2Cols(medicalTable, "Dị ứng (thuốc, thực phẩm):", isBlank(record.getAllergies()) ? "Không ghi nhận dị ứng" : record.getAllergies(), boldFont, regularFont);
        addTableRow2Cols(medicalTable, "Bệnh lý mãn tính:", isBlank(record.getChronicDisease()) ? "Không có" : record.getChronicDisease(), boldFont, regularFont);
        addTableRow2Cols(medicalTable, "Tiền sử điều trị / phẫu thuật:", isBlank(record.getTreatmentHistory()) ? "Bình thường" : record.getTreatmentHistory(), boldFont, regularFont);
        addTableRow2Cols(medicalTable, "Lịch sử tiêm chủng cơ bản:", isBlank(record.getVaccination()) ? "Đã tiêm chủng đầy đủ theo lứa tuổi" : record.getVaccination(), boldFont, regularFont);

        document.add(medicalTable);
        document.add(new Paragraph(" "));

        // 5. Section III: Khám thị lực & Chỉ số lâm sàng
        addSectionHeader(document, "III. CHỈ SỐ THỊ LỰC & SỨC KHỎE KHÁC", sectionTitleFont);

        PdfPTable clinicalTable = new PdfPTable(4);
        clinicalTable.setWidthPercentage(100);
        clinicalTable.setWidths(new float[]{22f, 28f, 22f, 28f});

        addTableRow(clinicalTable, "Thị lực:", record.getVision() + "/10", boldFont, regularFont);
        addTableRow(clinicalTable, "Thính lực:", isBlank(record.getHearing()) ? "Bình thường" : record.getHearing(), boldFont, regularFont);

        document.add(clinicalTable);

        if (!isBlank(record.getOther_health_info())) {
            PdfPTable otherTable = new PdfPTable(1);
            otherTable.setWidthPercentage(100);
            PdfPCell cOther = new PdfPCell(new Phrase("Ghi chú bổ sung: " + record.getOther_health_info(), noteFont));
            cOther.setPadding(6);
            cOther.setBackgroundColor(new Color(254, 243, 199)); // #fef3c7 light amber
            cOther.setBorderColor(new Color(253, 230, 138));
            otherTable.addCell(cOther);
            document.add(otherTable);
        }

        document.add(new Paragraph(" "));

        // 6. Section IV: Vùng ký xác nhận
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);
        signTable.setWidths(new float[]{50f, 50f});

        PdfPCell signLeft = new PdfPCell();
        signLeft.setBorder(Rectangle.NO_BORDER);
        Paragraph pSignParent = new Paragraph("Ý KIẾN XÁC NHẬN CỦA PHỤ HUYNH\n(Ký và ghi rõ họ tên)\n\n\n\n\n" + (parent != null ? parent.getFullName() : ""), boldFont);
        pSignParent.setAlignment(Element.ALIGN_CENTER);
        signLeft.addElement(pSignParent);
        signTable.addCell(signLeft);

        PdfPCell signRight = new PdfPCell();
        signRight.setBorder(Rectangle.NO_BORDER);
        String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("dd 'tháng' MM 'năm' yyyy"));
        Paragraph pSignNurse = new Paragraph("Hà Nội, ngày " + todayStr + "\nCÁN BỘ Y TẾ HỌC ĐƯỜNG\n(Ký và ghi rõ họ tên)\n\n\n\n\n(Đã xác nhận trên hệ thống)", boldFont);
        pSignNurse.setAlignment(Element.ALIGN_CENTER);
        signRight.addElement(pSignNurse);
        signTable.addCell(signRight);

        document.add(signTable);

        // 7. Footer Note
        document.add(new Paragraph(" "));
        Paragraph footerNote = new Paragraph("--------------------------------------------------------------------------------------------------------\n* Thẻ y tế điện tử được khởi tạo và lưu trữ trên Hệ Thống Quản Lý Y Tế Học Đường (School Medical System).", noteFont);
        footerNote.setAlignment(Element.ALIGN_CENTER);
        document.add(footerNote);

        document.close();
    }

    private void addSectionHeader(Document document, String title, Font font) throws DocumentException {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(title, font));
        cell.setBackgroundColor(BG_SECTION);
        cell.setBorderColor(PRIMARY_COLOR);
        cell.setBorderWidth(1.5f);
        cell.setPadding(6);
        table.addCell(cell);
        document.add(table);
    }

    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(5);
        labelCell.setBackgroundColor(new Color(248, 250, 252));
        labelCell.setBorderColor(BORDER_COLOR);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        valueCell.setPadding(5);
        valueCell.setBorderColor(BORDER_COLOR);
        table.addCell(valueCell);
    }

    private void addTableRow2Cols(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setPadding(6);
        labelCell.setBackgroundColor(new Color(248, 250, 252));
        labelCell.setBorderColor(BORDER_COLOR);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "-", valueFont));
        valueCell.setPadding(6);
        valueCell.setBorderColor(BORDER_COLOR);
        table.addCell(valueCell);
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
