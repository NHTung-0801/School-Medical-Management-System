package com.medical.schoolMedical.modules.pharmacy.controllers;

import com.medical.schoolMedical.modules.pharmacy.entities.MedicalSupply;
import com.medical.schoolMedical.modules.pharmacy.services.MedicalSupplyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/nurse/medical-supplies")
public class MedicalSupplyController {

    @Autowired
    private MedicalSupplyService medicalSupplyService;

    @GetMapping
    public String showMedicalSupplies(Model model) {
        List<MedicalSupply> supplyList = medicalSupplyService.getAllMedicalSupplies();
        model.addAttribute("supplyList", supplyList);
        return "nurse/medicalSupply/supply_list";
    }

    // Hiển thị form thêm vật tư
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("supply", new MedicalSupply());
        return "nurse/medicalSupply/supply_form";
    }

    @GetMapping("/form")
    public String showSupplyForm(@RequestParam(required = false) Long id, Model model) {
        MedicalSupply supply = (id != null) ? medicalSupplyService.getSupplyById(id) : new MedicalSupply();
        model.addAttribute("supply", supply);
        return "nurse/medicalSupply/supply_form";
    }

    // Hiển thị form sửa vật tư
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        MedicalSupply supply = medicalSupplyService.getSupplyById(id);
        if (supply == null) {
            return "redirect:/nurse/medical-supplies?error=Không tìm thấy vật tư";
        }
        model.addAttribute("supply", supply);
        return "nurse/medicalSupply/supply_form";
    }

    // Xử lý lưu vật tư
    @PostMapping("/save")
    public String saveSupply(@ModelAttribute("supply") @Valid MedicalSupply supply,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        boolean isNew = (supply.getId() == null);

        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return isNew ? "redirect:/nurse/medical-supplies/new" : "redirect:/nurse/medical-supplies/edit/" + supply.getId();
        }

        String normalizedName = supply.getName().trim();

        // Kiểm tra tên trùng
        if (isNew) {
            if (medicalSupplyService.existsByName(normalizedName)) {
                redirectAttributes.addFlashAttribute("error", "Vật tư với tên \"" + normalizedName + "\" đã tồn tại.");
                return "redirect:/nurse/medical-supplies/new";
            }
        } else {
            if (medicalSupplyService.isNameTakenByOtherId(normalizedName, supply.getId())) {
                redirectAttributes.addFlashAttribute("error", "Tên vật tư \"" + normalizedName + "\" đã được sử dụng bởi vật tư khác.");
                return "redirect:/nurse/medical-supplies/edit/" + supply.getId();
            }
        }

        supply.setName(normalizedName);
        medicalSupplyService.save(supply);
        redirectAttributes.addFlashAttribute("success", isNew ? "Thêm vật tư thành công." : "Cập nhật vật tư thành công.");

        return "redirect:/nurse/medical-supplies";
    }

    // Xóa vật tư
    @GetMapping("/delete/{id}")
    public String deleteSupply(@PathVariable("id") Long id) {
        medicalSupplyService.deleteById(id);
        return "redirect:/nurse/medical-supplies";
    }
}
