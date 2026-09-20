package com.medical.schoolMedical.modules.health_record.services;

import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.modules.health_record.entities.HealthRecord;
import com.medical.schoolMedical.modules.health_record.repositories.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthRecordService {
    @Autowired
    private HealthRecordRepository healthRecordRepository;

    public void save(HealthRecord record) {
        healthRecordRepository.save(record);
    }

    // Lấy hồ sơ sức khỏe theo ID
    public HealthRecord getById(Long id) {
        return healthRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.HEALTH_RECORD_NOT_FOUND));
    }

    // Lấy hồ sơ theo học sinh (nếu có)
    public Optional<HealthRecord> getByStudent(Student student) {
        return healthRecordRepository.findByStudent(student);
    }

    // Kiểm tra học sinh đã có hồ sơ chưa
    public boolean hasRecord(Student student) {
        return healthRecordRepository.existsByStudent(student);
    }

    // Xóa hồ sơ (nếu cần thiết)
    public void deleteById(Long id) {
        healthRecordRepository.deleteById(id);
    }

    public Optional<HealthRecord> findByStudentId(Long studentId) {
        return healthRecordRepository.findByStudent_Id(studentId);
    }

    // Lấy danh sách
    public List<HealthRecord> getAll() {
        return healthRecordRepository.findAll();
    }

    // Truy vấn tìm kiếm theo tên học sinh
    public List<HealthRecord> searchByStudentName(String keyword) {
        return healthRecordRepository.searchByStudentName(keyword);
    }

    public Optional<HealthRecord> findById(Long id) {
        return healthRecordRepository.findById(id);
    }

    public Optional<HealthRecord> findByIdWithStudentAndParent(Long id) {
        return healthRecordRepository.findByIdWithStudentAndParent(id);
    }
}
