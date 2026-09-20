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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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
    public String managerHome(@RequestParam(value = "year", required = false) Integer year,
                              Model model,
                              Authentication authentication) {
        String username = (authentication != null) ? authentication.getName() : "Guest";// Lấy tên người đăng nhập
        model.addAttribute("username", username);

        int currentYear = LocalDate.now().getYear();
        int selectedYear = (year != null && year >= 2020 && year <= 2100) ? year : currentYear;
        List<Integer> years = List.of(currentYear - 2, currentYear - 1, currentYear, currentYear + 1);

        List<Integer> vaccinationCounts = statisticsService.getMonthlyVaccinationCounts(selectedYear);
        List<Integer> healthCheckCounts = statisticsService.getMonthlyHealthCheckCounts(selectedYear);
        List<Integer> medicalEventCounts = statisticsService.getMonthlyMedicalEventCounts(selectedYear);

        log.info("selectedYear in managerHome: {}, medicalEventCounts: {}", selectedYear, medicalEventCounts);

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("years", years);
        model.addAttribute("vaccinationCounts", vaccinationCounts);
        model.addAttribute("healthCheckCounts", healthCheckCounts);
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
