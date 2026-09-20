package com.medical.schoolMedical.controller.schoolNurse;

import com.medical.schoolMedical.dto.VaccinationScheduleDTO;
import com.medical.schoolMedical.entities.VaccinationSchedule;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.security.CustomUserDetails;
import com.medical.schoolMedical.service.VaccinationConsentService;
import com.medical.schoolMedical.service.VaccinationScheduleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/nurse/vaccinationSchedule")
public class VaccinationScheduleController {

    @Autowired
    private VaccinationScheduleService vaccinationScheduleService;
    @Autowired
    private VaccinationConsentService vaccinationConsentService;

    @GetMapping
    public String vaccinationSchedule(Model model) {
        model.addAttribute("vaccinationSchedule", new VaccinationScheduleDTO());
        return "nurse/vaccinationScheduleNurse";

    }

//    Tạo lịch tiêm
    @PostMapping("/create")
    public String createVaccinationSchedule(@ModelAttribute("vaccinationSchedule") @Valid VaccinationScheduleDTO vaccinationScheduleDTO
                                            , BindingResult bindingResult
                                            ,@AuthenticationPrincipal CustomUserDetails customUserDetails
                                            , RedirectAttributes redirectAttributes
                                            , Model model) {
        log.info("Creating vaccination schedule: {}", vaccinationScheduleDTO);
        if (bindingResult.hasErrors()) {
            return "nurse/vaccinationScheduleNurse";
        }
        Long userId = customUserDetails.getUser().getId();
        try{
            VaccinationScheduleDTO result = vaccinationScheduleService.createVaccinationSchedule(vaccinationScheduleDTO, userId);
            redirectAttributes.addFlashAttribute("success", "Tạo lịch tiêm chủng thành công");
            return "redirect:/nurse/nurse-home";

        }catch (BusinessException e){
            redirectAttributes.addFlashAttribute("error", "Tạo lịch tiêm chủng thất bại");
            return "redirect:/nurse/nurse-home";
        }

    }

    //    Danh sách các lịch tiêm chủng đã tạo nhưng chưa gửi
    @GetMapping("/vaccinationSchedules/drafts")
    public String vaccinationScheduleDrafts_list(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<VaccinationScheduleDTO> vaccinationSchedules = vaccinationScheduleService.getAllVaccinationSchedule_drafts(page);
        model.addAttribute("draftsVaccinationSchedules",vaccinationSchedules.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vaccinationSchedules.getTotalPages());
        return  "nurse/vaccination-schedule-list";
    }

//    Gửi lịch tiêm đến phụ huynh
    @PostMapping("/sentToParent")
    public String sentVaccinationSchedule(@ModelAttribute("vaccinationScheduleId") Long vaccinationScheduleId
            , RedirectAttributes redirectAttributes
            , Model model) {

        VaccinationScheduleDTO vaccinationSchedule = vaccinationScheduleService.getVaccinationScheduleById(vaccinationScheduleId);
        log.info("vaccinationSchedule in sentVaccinationSchedule: {}", vaccinationSchedule);

        try{
//                    Gửi lịch đến phụ huynh:
            vaccinationConsentService.sendVaccinationSchedule_toParent(vaccinationSchedule);
            redirectAttributes.addFlashAttribute("success", "Gửi lịch tiêm vaccine đến parent thành công");
            return "redirect:/nurse/vaccinationSchedule/vaccinationSchedules/drafts";
        }catch (BusinessException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
            return "redirect:/nurse/nurse-home";
        }

    }

    //    Danh sách các lịch tiêm chủng đã gửi
    @GetMapping("/vaccinationSchedules/sent")
    public String vaccinationScheduleSent_list(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<VaccinationScheduleDTO> vaccinationSchedules = vaccinationScheduleService.getAllVaccinationSchedule_sent(page);
        model.addAttribute("sentVaccinationSchedules",vaccinationSchedules.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", vaccinationSchedules.getTotalPages());
        return  "nurse/ListSentVaccinationSchedules";
    }
}
