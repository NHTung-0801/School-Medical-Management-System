package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.enums.Gender;
import com.medical.schoolMedical.modules.user_management.services.ParentService;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/manager/students")
public class ManagerStudentController {
    @Autowired
    private ParentService parentService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/create")
    public String showCreateStudentForm(Model model) {
        model.addAttribute("parentList", parentService.getAllParents());
        return "manager/student_form";
    }

    @PostMapping("/create")
    public String createStudent(@RequestParam String fullName,
                                @RequestParam Gender gender,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
                                @RequestParam String address,
                                @RequestParam String className,
                                @RequestParam Long parentId,
                                RedirectAttributes redirectAttributes) {
        if (fullName == null || fullName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Họ và tên học sinh không được để trống.");
            return "redirect:/manager/students/create";
        }
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "Ngày sinh không hợp lệ hoặc nằm trong tương lai.");
            return "redirect:/manager/students/create";
        }
        if (className == null || className.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên lớp không được để trống.");
            return "redirect:/manager/students/create";
        }
        studentService.createStudent(fullName.trim(), gender, birthDate, address != null ? address.trim() : "", className.trim(), parentId);
        redirectAttributes.addFlashAttribute("success", "Thêm học sinh thành công!");
        return "redirect:/manager/students/list";
    }

    @GetMapping("/list")
    public String listStudents(Model model) {
        model.addAttribute("studentList", studentService.getAllStudents());
        return "manager/student_list";
    }
}
