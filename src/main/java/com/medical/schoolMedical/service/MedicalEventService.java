package com.medical.schoolMedical.service;


import com.medical.schoolMedical.dto.MedicalEventDTO;
import com.medical.schoolMedical.dto.MedicineUsedDTO;
import com.medical.schoolMedical.dto.SupplyUsedDTO;
import com.medical.schoolMedical.entities.*;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalEventService {

    private final MedicalEventRepository medicalEventRepository;

    private final StudentRepository studentRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final MedicineRepository medicineRepository;

    private final MedicalSupplyRepository medicalSupplyRepository;


    /*//Lưu và cập nhật sự kiện y tế
    public void saveMedicalEvent(MedicalEvent event) {
        medicalEventRepository.save(event);
    }*/

    @Transactional
    public void saveMedicalEvent(MedicalEvent event) {
        // Kiểm tra và trừ số lượng thuốc
        for (MedicineUsed medUsed : event.getMedicineUsed()) {
            Medicine medicine = medicineRepository.findById(medUsed.getMedicine().getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc"));

            int usedQty = medUsed.getQuantity();
            int availableQty = medicine.getQuantityInStock();

            if (usedQty > availableQty) {
                throw new IllegalArgumentException("Số lượng thuốc '" + medicine.getName() +
                        "' vượt quá tồn kho (" + availableQty + ")");
            }

            // Trừ tồn kho
            medicine.setQuantityInStock(availableQty - usedQty);
            medicineRepository.save(medicine);
        }

        // Kiểm tra và trừ số lượng vật tư y tế
        for (SupplyUsed supUsed : event.getSupplyUsed()) {
            MedicalSupply supply = medicalSupplyRepository.findById(supUsed.getMedicalSupply().getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEDICAL_SUPPLY_NOT_FOUND));

            int usedQty = supUsed.getQuantity();
            int availableQty = supply.getQuantityInStock();

            if (usedQty > availableQty) {
                throw new IllegalArgumentException("Số lượng vật tư '" + supply.getName() +
                        "' vượt quá tồn kho (" + availableQty + ")");
            }

            // Trừ tồn kho
            supply.setQuantityInStock(availableQty - usedQty);
            medicalSupplyRepository.save(supply);
        }

        // Lưu sự kiện y tế (sau khi trừ kho thành công)
        medicalEventRepository.save(event);
    }


    //Lấy tất cả sự kiện y tế
    public List<MedicalEvent> getAllMedicalEvents() {
        return medicalEventRepository.findAll();
    }

    //Tìm sự kiện y tế theo id
    public MedicalEvent findMedicalEventById(Long id) {
        return medicalEventRepository.findById(id).orElse(null);
    }

    //Tìm sự kiên của một học sinh
    public List<MedicalEvent> findByStudentId(Long studentId) {
        return medicalEventRepository.findByStudentId(studentId);
    }

    //Tìm các sự kiện do một y tá phụ trách
    public List<MedicalEvent> findByNurseId(Long nurseId) {
        return medicalEventRepository.findBySchoolNurseId(nurseId);
    }

    //Xóa sự kiện
    public void deleteMedicalEventById(Long id) {
        medicalEventRepository.deleteById(id);
    }


    // Chuyển từ Entity sang DTO
    public MedicalEventDTO convertToDto(MedicalEvent event) {
        MedicalEventDTO dto = new MedicalEventDTO();
        dto.setId(event.getId());
        dto.setStudentId(event.getStudent().getId());
        dto.setStudentFullName(event.getStudent().getFullName());
        dto.setNurseId(event.getSchoolNurse().getId());
        dto.setNurseFullName(event.getSchoolNurse().getFullName());
        dto.setLocation(event.getLocation());
        dto.setDescription(event.getDescription());
        dto.setInitialTreatment(event.getInitial_treatment());
        dto.setFinalTreatment(event.getFinal_treatment());
        dto.setNotes(event.getNotes());
        dto.setEventTime(event.getEventTime());

        // Medicines used
        if (event.getMedicineUsed() != null) {
            List<MedicineUsedDTO> medicinesUsed = event.getMedicineUsed().stream().map(mu -> {
                MedicineUsedDTO muDto = new MedicineUsedDTO();
                muDto.setMedicineId(mu.getMedicine().getId());
                muDto.setMedicineName(mu.getMedicine().getName());
                muDto.setQuantity(mu.getQuantity());
                muDto.setNotes(mu.getNotes());
                return muDto;
            }).collect(Collectors.toList());
            dto.setMedicinesUsed(medicinesUsed);
        }

        // Supplies used
        if (event.getSupplyUsed() != null) {
            List<SupplyUsedDTO> suppliesUsed = event.getSupplyUsed().stream().map(su -> {
                SupplyUsedDTO suDto = new SupplyUsedDTO();
                suDto.setSupplyId(su.getMedicalSupply().getId());
                suDto.setSupplyName(su.getMedicalSupply().getName());
                suDto.setQuantity(su.getQuantity());
                suDto.setNotes(su.getNotes());
                return suDto;
            }).collect(Collectors.toList());
            dto.setSuppliesUsed(suppliesUsed);
        }

        return dto;
    }

    // Chuyển từ DTO sang Entity để tạo mới
    public MedicalEvent convertFromDto(MedicalEventDTO dto, User user, SchoolNurse nurse) {
        MedicalEvent event = new MedicalEvent();
        event.setUser(user);
        event.setSchoolNurse(nurse);

        Student student = userService.findStudentById(dto.getStudentId());
        event.setStudent(student);
        event.setLocation(dto.getLocation());
        event.setDescription(dto.getDescription());
        event.setInitial_treatment(dto.getInitialTreatment());
        event.setFinal_treatment(dto.getFinalTreatment());
        event.setNotes(dto.getNotes());


        Set<MedicineUsed> medicinesUsed = dto.getMedicinesUsed().stream()
                .filter(m -> m.getMedicineId() != null)
                .map(muDto -> {
                    MedicineUsed mu = new MedicineUsed();
                    mu.setQuantity(muDto.getQuantity());
                    mu.setNotes(muDto.getNotes());
                    mu.setMedicalEvent(event);
                    mu.setMedicine(medicineRepository.findById(muDto.getMedicineId()).orElseThrow());
                    return mu;
                }).collect(Collectors.toSet());

        event.setMedicineUsed(medicinesUsed);


        Set<SupplyUsed> supplyUsedList = dto.getSuppliesUsed().stream()
                .filter(s -> s.getSupplyId() != null)
                .map(supplyDto -> {
                    SupplyUsed su = new SupplyUsed();
                    MedicalSupply supply = medicalSupplyRepository.findById(supplyDto.getSupplyId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư ID: " + supplyDto.getSupplyId()));
                    su.setMedicalSupply(supply);
                    su.setQuantity(supplyDto.getQuantity());
                    su.setNotes(supplyDto.getNotes());
                    su.setMedicalEvent(event);
                    return su;
                }).collect(Collectors.toSet());
        event.setSupplyUsed(supplyUsedList);

        return event;
    }



    // Cập nhật từ DTO vào entity đã tồn tại
    public void updateFromDto(MedicalEventDTO dto) {
        MedicalEvent event = medicalEventRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + dto.getId()));

        Student student = userService.findStudentById(dto.getStudentId());
        event.setStudent(student);
        event.setLocation(dto.getLocation());
        event.setDescription(dto.getDescription());
        event.setInitial_treatment(dto.getInitialTreatment());
        event.setFinal_treatment(dto.getFinalTreatment());
        event.setNotes(dto.getNotes());

        Set<MedicineUsed> medicineUsedList = dto.getMedicinesUsed().stream()
                .filter(m -> m.getMedicineId() != null)
                .map(medDto -> {
                    MedicineUsed mu = new MedicineUsed();
                    Medicine medicine = medicineRepository.findById(medDto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc ID: " + medDto.getMedicineId()));
                    mu.setMedicine(medicine);
                    mu.setQuantity(medDto.getQuantity());
                    mu.setNotes(medDto.getNotes());
                    mu.setMedicalEvent(event);
                    return mu;
                }).collect(Collectors.toSet());
        event.setMedicineUsed(medicineUsedList);

        Set<SupplyUsed> supplyUsedList = dto.getSuppliesUsed().stream()
                .filter(s -> s.getSupplyId() != null)
                .map(supplyDto -> {
                    SupplyUsed su = new SupplyUsed();
                    MedicalSupply supply = medicalSupplyRepository.findById(supplyDto.getSupplyId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư ID: " + supplyDto.getSupplyId()));
                    su.setMedicalSupply(supply);
                    su.setQuantity(supplyDto.getQuantity());
                    su.setNotes(supplyDto.getNotes());
                    su.setMedicalEvent(event);
                    return su;
                }).collect(Collectors.toSet());
        event.setSupplyUsed(supplyUsedList);

        medicalEventRepository.save(event);
    }


    public void updateMedicalEvent(Long id, MedicalEvent updatedEvent) {
        MedicalEvent existing = medicalEventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sự kiện"));

        existing.setStudent(updatedEvent.getStudent());
        existing.setLocation(updatedEvent.getLocation());
        existing.setDescription(updatedEvent.getDescription());
        existing.setInitial_treatment(updatedEvent.getInitial_treatment());
        existing.setFinal_treatment(updatedEvent.getFinal_treatment());
        existing.setNotes(updatedEvent.getNotes());

        // Xoá và thêm lại danh sách thuốc
        existing.getMedicineUsed().clear();
        existing.getMedicineUsed().addAll(updatedEvent.getMedicineUsed());

        // Xoá và thêm lại danh sách vật tư
        existing.getSupplyUsed().clear();
        existing.getSupplyUsed().addAll(updatedEvent.getSupplyUsed());

        medicalEventRepository.save(existing);
    }

    // Chuyển từ DTO sang entity
    public MedicalEvent convertToEntity(MedicalEventDTO dto) {
        MedicalEvent event = medicalEventRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sự kiện với ID: " + dto.getId()));

        // Cập nhật các thông tin cơ bản
        Student student = userService.findStudentById(dto.getStudentId());
        event.setStudent(student);

        event.setLocation(dto.getLocation());
        event.setDescription(dto.getDescription());
        event.setInitial_treatment(dto.getInitialTreatment());
        event.setFinal_treatment(dto.getFinalTreatment());
        event.setNotes(dto.getNotes());

        // Xử lý danh sách thuốc sử dụng
        Set<MedicineUsed> medicinesUsed = dto.getMedicinesUsed().stream()
                .filter(m -> m.getMedicineId() != null)
                .map(medDto -> {
                    MedicineUsed mu = new MedicineUsed();
                    Medicine medicine = medicineRepository.findById(medDto.getMedicineId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy thuốc ID: " + medDto.getMedicineId()));
                    mu.setMedicine(medicine);
                    mu.setQuantity(medDto.getQuantity());
                    mu.setNotes(medDto.getNotes());
                    mu.setMedicalEvent(event); // gán liên kết ngược
                    return mu;
                }).collect(Collectors.toSet());
        event.setMedicineUsed(medicinesUsed);

        // Xử lý danh sách vật tư y tế sử dụng
        Set<SupplyUsed> suppliesUsed = dto.getSuppliesUsed().stream()
                .filter(s -> s.getSupplyId() != null) // tránh lỗi ID null
                .map(supplyDto -> {
                    SupplyUsed su = new SupplyUsed();
                    MedicalSupply supply = medicalSupplyRepository.findById(supplyDto.getSupplyId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy vật tư ID: " + supplyDto.getSupplyId()));
                    su.setMedicalSupply(supply);
                    su.setQuantity(supplyDto.getQuantity());
                    su.setNotes(supplyDto.getNotes());
                    su.setMedicalEvent(event); // gán liên kết ngược
                    return su;
                }).collect(Collectors.toSet());
        event.setSupplyUsed(suppliesUsed);

        return event;
    }

    public void deleteById(Long id) {
        medicalEventRepository.deleteById(id);
    }



}
