package com.medical.schoolMedical.controller.parent;

import com.medical.schoolMedical.dto.VaccinationRecordDTO;
import com.medical.schoolMedical.entities.VaccinationRecord;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.service.VaccinationRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/parent/vaccinationRecord")
public class VaccinationRecordParentController {
    @Autowired
    private VaccinationRecordService vaccinationRecordService;

    @GetMapping
    public String getVaccinationRecordParent(Model model
            , RedirectAttributes redirectAttributes
            , @RequestParam(value = "idRecord", required = false ) Long idRecord){
        if(idRecord == null){
            redirectAttributes.addFlashAttribute("error","Vui lòng chọn bản ghi tiêm chủng phù hợp để xem chi tiết.");
            return "redirect:/parent/notification/VaccinationRecords";
        }

        try{
            VaccinationRecordDTO vaccinationRecordDTO = vaccinationRecordService.getVaccinationRecord_updateViewed(idRecord);
            model.addAttribute("vaccinationRecord", vaccinationRecordDTO);
//            log.info("vaccinationRecordDTO in getHealthCheckRecordParent ==>: {}", vaccinationRecordDTO);
            return "parent/vaccinationRecordParent";
        }catch (BusinessException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
            return "redirect:/parent/notification/VaccinationRecords";
        }


    }
}
