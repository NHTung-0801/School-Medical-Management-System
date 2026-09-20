package com.medical.schoolMedical.modules.user_management.services;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.modules.user_management.repositories.SchoolNurseRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SchoolNurseService {

    SchoolNurseRepository schoolNurseRepository;

    public SchoolNurse getByUserId(Long userId) {
        return schoolNurseRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy y tá với userId " + userId));
    }

    public SchoolNurse findNurseByUsername(String username) {
        return schoolNurseRepository.findByUser_Username(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy y tá với username: " + username));
    }
}
