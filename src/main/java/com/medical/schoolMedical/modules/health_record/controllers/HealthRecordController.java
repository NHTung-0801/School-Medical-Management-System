package com.medical.schoolMedical.modules.health_record.controllers;

import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.health_record.entities.HealthRecord;
import com.medical.schoolMedical.modules.health_record.services.HealthRecordService;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/parent/health-record")
public class HealthRecordController {
    @Autowired
    private HealthRecordService healthRecordService;

    @Autowired
    private UserService userService;

    // Giao diện chọn học sinh (nếu phụ huynh có nhiều con)
    @GetMapping("/select-student")
    public String selectStudent(Model model) {
        Parent parent = userService.getCurrentParent();
        Long parentId = parent.getId();

        List<Student> students = userService.getStudentsByParentId(parentId);

        model.addAttribute("students", students);
        return "parent/health-record/select_student";
    }

    // Hiển thị form khai báo hoặc cập nhật
    @GetMapping("/form/{studentId}")
    public String showForm(@PathVariable Long studentId, Model model) {
        Student student = userService.findStudentById(studentId);

        if (student == null) {
            return "redirect:/parent/health-record/select-student";
        }

        Optional<HealthRecord> optionalRecord = healthRecordService.getByStudent(student);
        HealthRecord existingRecord = optionalRecord.orElseGet(() -> {
            HealthRecord newRecord = new HealthRecord();
            newRecord.setStudent(student);
            newRecord.setParent(userService.getCurrentParent());
            return newRecord;
        });

        model.addAttribute("record", existingRecord);
        return "parent/health-record/health_record_form";
    }

    // Lưu hoặc cập nhật hồ sơ sức khỏe
    @PostMapping("/save")
    public String saveRecord(@ModelAttribute("record") HealthRecord record,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (record.getStudent() == null || record.getStudent().getId() == null) {
            redirectAttributes.addFlashAttribute("error", "Thông tin học sinh không hợp lệ.");
            return "redirect:/parent/health-record/select-student";
        }

        Student student = userService.findStudentById(record.getStudent().getId());
        Parent currentParent = userService.getCurrentParent();

        if (student == null || student.getParent() == null || student.getParent().getId() != currentParent.getId()) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền lưu hồ sơ cho học sinh này.");
            return "redirect:/parent/health-record/select-student";
        }

        // Ràng buộc thị lực (vision) phải từ 0 đến 10
        if (record.getVision() < 0 || record.getVision() > 10) {
            model.addAttribute("error", "Giá trị thị lực phải từ 0 đến 10.");
            model.addAttribute("record", record);
            return "parent/health-record/health_record_form";
        }

        record.setParent(currentParent);
        record.setStudent(student);
        healthRecordService.save(record);

        redirectAttributes.addFlashAttribute("success", "Lưu hồ sơ sức khỏe thành công.");
        return "redirect:/parent/health-record/select-student";
    }

    // Xem chi tiết hồ sơ
    @GetMapping("/view/{studentId}")
    public String viewRecord(@PathVariable Long studentId, Model model) {
        Student student = userService.findStudentById(studentId);
        if (student == null) {
            return "redirect:/parent/health-record/select-student";
        }

        Optional<HealthRecord> optionalRecord = healthRecordService.getByStudent(student);
        if (optionalRecord.isEmpty()) {
            return "redirect:/parent/health-record/form/" + studentId;
        }

        model.addAttribute("record", optionalRecord.get());
        return "parent/health-record/health_record_view";
    }

    // Xoá hồ sơ sức khỏe
    @PostMapping("/delete/{id}")
    public String deleteHealthRecord(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Parent currentParent = userService.getCurrentParent();
        Optional<HealthRecord> recordOpt = healthRecordService.findByIdWithStudentAndParent(id);
        if (recordOpt.isEmpty() || recordOpt.get().getParent().getId() != currentParent.getId()) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền xoá hồ sơ này.");
            return "redirect:/parent/health-record/select-student";
        }
        healthRecordService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xoá hồ sơ.");
        return "redirect:/parent/health-record/select-student";
    }
}
