package com.medical.schoolMedical.modules.medical_event.controllers;

import com.medical.schoolMedical.entities.SchoolNurse;
import com.medical.schoolMedical.entities.User;
import com.medical.schoolMedical.modules.medical_event.dto.MedicalEventDTO;
import com.medical.schoolMedical.modules.medical_event.entities.MedicalEvent;
import com.medical.schoolMedical.modules.medical_event.services.MedicalEventService;
import com.medical.schoolMedical.modules.pharmacy.dto.MedicineUsedDTO;
import com.medical.schoolMedical.modules.pharmacy.dto.MedicineUsedRequestDTO;
import com.medical.schoolMedical.modules.pharmacy.dto.SupplyUsedDTO;
import com.medical.schoolMedical.modules.pharmacy.services.MedicalSupplyService;
import com.medical.schoolMedical.modules.pharmacy.services.MedicineService;
import com.medical.schoolMedical.modules.pharmacy.services.MedicineUsedService;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.service.StudentService;
import com.medical.schoolMedical.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/nurse/medical-events")
@RequiredArgsConstructor
public class MedicalEventController {

    @Autowired
    private MedicalEventService medicalEventService;

    @Autowired
    private UserService userService;

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private MedicalSupplyService medicalSupplyService;

    @Autowired
    private MedicineUsedService medicineUsedService;

    @Autowired
    private StudentService studentService;

    // Danh sách sự kiện y tế
    @GetMapping
    public String viewAllEvents(@AuthenticationPrincipal CustomUserDetails currentUser,
                                Model model) {
        SchoolNurse nurse = userService.findNurseByUsername(currentUser.getUsername());
        List<MedicalEvent> events = medicalEventService.findByNurseId(nurse.getId());

        List<MedicalEventDTO> dtoList = events.stream().map(event -> {
            MedicalEventDTO dto = new MedicalEventDTO();
            dto.setId(event.getId());
            dto.setStudentFullName(event.getStudent().getFullName());
            dto.setEventTime(event.getEventTime());
            dto.setLocation(event.getLocation());
            dto.setDescription(event.getDescription());
            return dto;
        }).toList();

        model.addAttribute("events", dtoList);
        return "nurse/list";
    }

    // Trang form thêm sự kiện y tế
    @GetMapping("/create")
    public String showCreateForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MedicalEventDTO dto = new MedicalEventDTO();
        dto.getMedicinesUsed().add(new MedicineUsedDTO()); // Chỉ 1 dòng thuốc
        dto.getSuppliesUsed().add(new SupplyUsedDTO());    // Chỉ 1 dòng vật tư

        model.addAttribute("eventDTO", dto);
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("supplies", medicalSupplyService.getAllSupplies());
        return "nurse/create";
    }

    @PostMapping("/save")
    public String saveMedicalEvent(@ModelAttribute("eventDTO") @Valid MedicalEventDTO dto,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal CustomUserDetails currentUser,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("eventDTO", dto);
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("supplies", medicalSupplyService.getAllMedicalSupplies());
            return "nurse/create";
        }
        try {
            // Tìm user và nurse đang login
            SchoolNurse nurse = userService.findNurseByUsername(currentUser.getUsername());
            User createdBy = userService.findByUsername(currentUser.getUsername());

            // Chuyển từ DTO sang Entity đầy đủ
            MedicalEvent event = medicalEventService.convertFromDto(dto, createdBy, nurse);

            // Lưu cả event và các thuốc/vật tư đi kèm
            medicalEventService.saveMedicalEvent(event);

            redirectAttributes.addFlashAttribute("success", "Ghi nhận sự kiện thành công!");
            return "redirect:/nurse/medical-events";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("eventDTO", dto);
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("medicines", medicineService.getAllMedicines());
            model.addAttribute("supplies", medicalSupplyService.getAllMedicalSupplies());

            return "nurse/create";
        }
    }

    @GetMapping("/{id}")
    public String viewEventDetail(@PathVariable Long id, Model model) {
        MedicalEvent event = medicalEventService.findMedicalEventById(id);
        MedicalEventDTO dto = medicalEventService.convertToDto(event);

        model.addAttribute("event", dto);
        return "nurse/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        MedicalEvent event = medicalEventService.findMedicalEventById(id);
        MedicalEventDTO dto = medicalEventService.convertToDto(event);

        model.addAttribute("event", dto);
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("medicines", medicineService.getAllMedicines());
        model.addAttribute("supplies", medicalSupplyService.getAllSupplies());

        return "nurse/edit";
    }

    @PostMapping("/update")
    public String updateEvent(@ModelAttribute("event") MedicalEventDTO dto,
                              RedirectAttributes redirectAttributes) {
        medicalEventService.updateFromDto(dto);
        redirectAttributes.addFlashAttribute("success", "Cập nhật sự kiện thành công!");
        return "redirect:/nurse/medical-events";
    }

    @PostMapping("/{eventId}/use-medicine")
    public ResponseEntity<String> useMedicine(
            @PathVariable Long eventId,
            @RequestBody MedicineUsedRequestDTO request) {
        medicineUsedService.useMedicine(
                eventId,
                request.getMedicineId(),
                request.getQuantity(),
                request.getNotes()
        );
        return ResponseEntity.ok("Đã ghi nhận sử dụng thuốc");
    }

    @GetMapping("/{eventId}/used-medicines")
    public ResponseEntity<List<MedicineUsedDTO>> getUsedMedicines(@PathVariable Long eventId) {
        return ResponseEntity.ok(medicineUsedService.getUsedMedicinesByEvent(eventId));
    }

    @PostMapping("/delete/{id}")
    public String deleteMedicalEvent(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            medicalEventService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa sự kiện thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sự kiện!");
        }
        return "redirect:/nurse/medical-events";
    }
}
