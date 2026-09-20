package com.medical.schoolMedical.modules.pharmacy.controllers;

import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.pharmacy.dto.SentMedicineDTO;
import com.medical.schoolMedical.modules.pharmacy.entities.SentMedicine;
import com.medical.schoolMedical.modules.pharmacy.services.SentMedicineService;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.modules.user_management.services.ParentService;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/parent/sentMedicine")
public class SentMedicineController {

    @Autowired
    private SentMedicineService sentMedicineService;

    @Autowired
    private ParentService parentService;

    @Autowired
    private StudentService studentService;

    // Hiển thị form gửi thuốc
    @GetMapping("/form")
    public String showForm(Model model,
                           @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Gọi theo userId
        Parent parent = parentService.getByUserId(userDetails.getId());

        if (parent == null) {
            throw new BusinessException(ErrorCode.PARENT_NOT_EXISTS);
        }

        // Lấy danh sách học sinh của phụ huynh
        List<Student> studentList = studentService.getByParentId(parent.getId());

        model.addAttribute("sentMedicineDTO", new SentMedicineDTO());
        model.addAttribute("studentList", studentList);

        return "parent/sent-medicine/sent_medicine_form";
    }

    // Xử lý submit gửi thuốc
    @PostMapping("/submit")
    public String submitSentMedicine(@ModelAttribute SentMedicineDTO dto,
                                     @AuthenticationPrincipal CustomUserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {

        Parent parent = parentService.getByUserId(userDetails.getId());

        if (parent == null) {
            throw new BusinessException(ErrorCode.PARENT_NOT_EXISTS);
        }

        Student student = studentService.getStudentById(dto.getStudentId());
        if (student == null) {
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND);
        }

        if (student.getParent() == null || student.getParent().getId() != parent.getId()) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền gửi thuốc cho học sinh này!");
            return "redirect:/parent/sentMedicine/form";
        }

        // Tạo entity từ DTO
        SentMedicine sent = new SentMedicine();
        sent.setParent(parent);
        sent.setStudent(student);
        sent.setMedicineList(dto.getMedicineList());
        sent.setUsageInstructions(dto.getUsageInstructions());

        sentMedicineService.create(sent);
        redirectAttributes.addFlashAttribute("success", "Gửi thuốc thành công!");

        return "redirect:/parent/sentMedicine/list";
    }

    // Xem danh sách thuốc đã gửi
    @GetMapping("/list")
    public String listSentMedicines(Model model,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getId();

        List<SentMedicine> list = sentMedicineService.getByParentUserId(userId);
        model.addAttribute("sentList", list);

        return "parent/sent-medicine/sent_medicine_list";
    }
}
