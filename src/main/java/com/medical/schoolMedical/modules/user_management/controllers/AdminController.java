package com.medical.schoolMedical.modules.user_management.controllers;

import com.medical.schoolMedical.enums.Role;
import com.medical.schoolMedical.modules.user_management.dto.StudentDTO;
import com.medical.schoolMedical.modules.user_management.entities.Admin;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.modules.user_management.entities.User;
import com.medical.schoolMedical.modules.user_management.repositories.AdminRepository;
import com.medical.schoolMedical.modules.user_management.services.StatisticsService;
import com.medical.schoolMedical.modules.user_management.services.StudentService;
import com.medical.schoolMedical.modules.user_management.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.medical.schoolMedical.util.ValidationUtil;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StudentService studentService;

    @GetMapping({"/dashboard", "/"})
    public String admin(@RequestParam(value = "year", required = false) Integer year,
                        Model model,
                        Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);

        int currentYear = LocalDate.now().getYear();
        int selectedYear = (year != null && year >= 2020 && year <= 2100) ? year : currentYear;
        List<Integer> years = List.of(currentYear - 2, currentYear - 1, currentYear, currentYear + 1);

        List<Integer> vaccinationCounts = statisticsService.getMonthlyVaccinationCounts(selectedYear);
        List<Integer> healthCheckCounts = statisticsService.getMonthlyHealthCheckCounts(selectedYear);
        List<Integer> medicalEventCounts = statisticsService.getMonthlyMedicalEventCounts(selectedYear);

        log.info("selectedYear: {}, vaccinationCounts: {}", selectedYear, vaccinationCounts);
        log.info("healthCheckCounts: {}", healthCheckCounts);
        log.info("medicalEventCounts: {}", medicalEventCounts);

        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("years", years);
        model.addAttribute("vaccinationCounts", vaccinationCounts);
        model.addAttribute("healthCheckCounts", healthCheckCounts);
        model.addAttribute("medicalEventCounts", medicalEventCounts);

        // Thống kê số lượng phụ huynh, hs, nurse ... trong hệ thống
        Map<String, Long> counts = statisticsService.getUserCountsByRole();
        model.addAttribute("counts", counts);

        List<StudentDTO> studentsThisMonth = studentService.getStudentsCreatedThisMonth();
        List<StudentDTO> studentsLastMonth = studentService.getStudentsCreatedLastMonth();
        log.info("studentsLastMonth in showChart {}:", studentsLastMonth);
        log.info("studentsThisMonth in showChart {}:", studentsThisMonth);

        model.addAttribute("studentsLastMonth", studentsLastMonth);
        model.addAttribute("studentsThisMonth", studentsThisMonth);

        return "admin/dashboard";
    }

    @GetMapping("/manage-users")
    public String manageUsers(Model model, Authentication authentication) {
        String username = authentication.getName();
        model.addAttribute("username", username);
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);

        model.addAttribute("newUser", new User());
        model.addAttribute("roles", Role.values());
        return "admin/manage-users";
    }

    @GetMapping("/edit-user/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("editUser", user);
        model.addAttribute("roles", Role.values());
        return "admin/edit-user";
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute("editUser") User user,
                             @RequestParam(value = "newPassword", required = false) String newPassword,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User existing = userService.findById(user.getId());
        if (existing == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Người dùng không tồn tại!");
            return "redirect:/admin/manage-users";
        }

        // Đảm bảo tên đăng nhập luôn giữ nguyên theo tài khoản trong cơ sở dữ liệu
        user.setUsername(existing.getUsername());

        // Ngăn chặn admin tự tước quyền ADMIN của chính mình
        if (authentication != null && existing.getUsername().equals(authentication.getName()) && user.getRole() != Role.ADMIN) {
            model.addAttribute("editUser", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", "Bạn không thể tự hạ quyền quản trị viên (ADMIN) của chính mình!");
            return "admin/edit-user";
        }

        String newEmail = (user.getEmail() != null) ? user.getEmail().trim() : "";

        // Kiểm tra định dạng email
        if (newEmail.isEmpty() || !ValidationUtil.isValidEmail(newEmail)) {
            model.addAttribute("editUser", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", "Email không đúng định dạng!");
            return "admin/edit-user";
        }

        // Kiểm tra trùng lặp email với tài khoản khác
        if (!newEmail.equalsIgnoreCase(existing.getEmail()) && userService.existsUserByEmail(newEmail)) {
            model.addAttribute("editUser", user);
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", "Email này đã được sử dụng bởi tài khoản khác!");
            return "admin/edit-user";
        }

        // Kiểm tra mật khẩu mới nếu người dùng muốn đổi
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            String pwd = newPassword.trim();
            if (pwd.length() < 5 || pwd.length() > 30 || pwd.contains(" ")) {
                model.addAttribute("editUser", user);
                model.addAttribute("roles", Role.values());
                model.addAttribute("errorMessage", "Mật khẩu mới phải từ 5-30 ký tự và không chứa khoảng trắng!");
                return "admin/edit-user";
            }
            existing.setPassword(passwordEncoder.encode(pwd));
        }

        // Cập nhật các trường được phép thay đổi
        existing.setEmail(newEmail);
        existing.setRole(user.getRole());
        userService.saveUser(existing);

        redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        return "redirect:/admin/manage-users";
    }

    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        User userToDelete = userService.findById(id);
        if (userToDelete != null && userToDelete.getUsername().equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn không thể tự xoá tài khoản quản trị viên của chính mình!");
            return "redirect:/admin/manage-users";
        }
        userService.softDeleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Xoá người dùng thành công!");
        return "redirect:/admin/manage-users";
    }
}
