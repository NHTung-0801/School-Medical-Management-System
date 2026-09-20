package com.medical.schoolMedical.controller.schoolNurse;

import com.medical.schoolMedical.dto.VaccinationConsentDTO;
import com.medical.schoolMedical.entities.VaccinationConsent;
import com.medical.schoolMedical.service.VaccinationConsentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/nurse/vaccinationConsent")
public class VaccinationConsentController {
    @Autowired
    VaccinationConsentService vaccinationConsentService;

    //    Lấy danh sách các học sinh đc phụ huynh đồng ý cho khám sức khỏe nhưng chưa khám
    @GetMapping("/list-student-vaccination")
    public String listStudent_vaccination(@RequestParam(value = "vaccinatonScheduleId", required = false) Long idSchedule,
                                          @RequestParam(defaultValue = "0") int page,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        if(idSchedule==null){
            redirectAttributes.addFlashAttribute("error","idSchedule không được để trống");
            return "redirect:/nurse/vaccinationSchedule/vaccinationSchedules";
        }

        boolean is_vaccinated = false;

        Page<VaccinationConsentDTO> consentPage  = vaccinationConsentService.getStudentsVaccination(idSchedule, page,is_vaccinated);
        log.info("consentPage={}",consentPage.getContent());

        model.addAttribute("consentPage", consentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", consentPage.getTotalPages());
        model.addAttribute("idSchedule", idSchedule);
        return  "nurse/listStudentVaccination";
    }

    //    Lấy danh sách các học sinh đc phụ huynh đồng ý cho tiêm chủng và đã được tiêm
    @GetMapping("/list-student-vaccination/vaccinated")
    public String listStudentVaccinated(@RequestParam(value = "vaccinatonScheduleId", required = false) Long idSchedule,
                                            @RequestParam(defaultValue = "0") int page,
                                            RedirectAttributes redirectAttributes,
                                            Model model){
        if(idSchedule==null){
            redirectAttributes.addFlashAttribute("error","idSchedule không được để trống");
            return "redirect:/nurse/vaccinationSchedule/vaccinationSchedules";
        }

        boolean is_vaccinated = true;
        Page<VaccinationConsentDTO> consentPage  = vaccinationConsentService.getStudentsVaccination(idSchedule, page,is_vaccinated);
        log.info("consentPage in listStudentVaccinated: {}",consentPage.getContent());


        model.addAttribute("consentPage", consentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", consentPage.getTotalPages());
        model.addAttribute("idSchedule", idSchedule);

        return  "nurse/danhSachStuden_daTiemChung";
    }

}
