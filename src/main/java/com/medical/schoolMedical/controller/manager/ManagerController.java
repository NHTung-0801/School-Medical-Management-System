package com.medical.schoolMedical.controller.manager;

import com.medical.schoolMedical.dto.StudentDTO;
import com.medical.schoolMedical.service.StatisticsService;
import com.medical.schoolMedical.service.StudentService;
import com.medical.schoolMedical.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/manager")
public class ManagerController {
    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StudentService studentService;
    @GetMapping("/manager-home")
    public String managerHome(Model model, Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "Guest";// Lấy tên người đăng nhập
        model.addAttribute("username", username);

        //        Tinhs toán thống kê
//        List<Integer> years = List.of(2023, 2024, 2025);
        List<Integer> vaccinationCounts = statisticsService.getMonthlyVaccinationCounts(2025);
        List<Integer> healthCheckCounts = statisticsService.getMonthlyHealthCheckCounts(2025);
        List<Integer> medicalEventCounts = statisticsService.getMonthlyMedicalEventCounts(2025);

        log.info("medicalEventCounts in showChart {}:" , medicalEventCounts);

//        model.addAttribute("years", years);
//        model.addAttribute("selectedYear", year);

//        data biểu đồ healthCheck và Vaccination
        model.addAttribute("vaccinationCounts", vaccinationCounts);
        model.addAttribute("healthCheckCounts", healthCheckCounts);
//        Biểu đồ medicalEventCounts
        model.addAttribute("medicalEventCounts", medicalEventCounts);

//        thống kê số lượng phụ huynh, hs, nurse ... tronbg hệ thống
        Map<String, Long> counts = statisticsService.getUserCountsByRole();
        model.addAttribute("counts", counts);

        List<StudentDTO> studentsThisMonth = studentService.getStudentsCreatedThisMonth();
        List<StudentDTO> studentsLastMonth = studentService.getStudentsCreatedLastMonth();
        log.info("studentsLastMonth in showChart {}:" , studentsLastMonth);
        log.info("studentsThisMonth in showChart {}:" , studentsThisMonth);

        model.addAttribute("studentsLastMonth", studentsLastMonth);
        model.addAttribute("studentsThisMonth", studentsThisMonth);
        return "manager/manager-home"; // Trả về view: templates/manager
    }
}
