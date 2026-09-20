package com.medical.schoolMedical.service;

import com.medical.schoolMedical.dto.ParentDTO;
import com.medical.schoolMedical.dto.UserDTO;
import com.medical.schoolMedical.entities.Parent;
import com.medical.schoolMedical.entities.User;
import com.medical.schoolMedical.exceptions.BusinessException;
import com.medical.schoolMedical.exceptions.ErrorCode;
import com.medical.schoolMedical.mapper.ParentMapper;
import com.medical.schoolMedical.mapper.UserMapper;
import com.medical.schoolMedical.repositories.ParentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
public class ParentService {

    ParentRepository parentRepository;
    ParentMapper parentMapper;

    public ParentDTO getParentbyId(Long userId) {
        Parent parent = parentRepository.findByUser_Id(userId).orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_EXISTS));
        return parentMapper.toParentDTO(parent);
    }

    // Lấy parent bằng userId
    public Parent getByUserId(Long userId) {
        return parentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_EXISTS));
    }

    // Lấy parent bằng username
    public Parent getByUsername(String username) {
        return parentRepository.findByUser_Username(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_NOT_EXISTS));
    }

    /*// Lấy parent bằng userId
    public Parent getByUserId(Long userId) {
        return parentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_USERID_NOT_FOUND));
    }

    // Lấy parent bằng username
    public Parent getByUsername(String username) {
        return parentRepository.findByUser_Username(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.PARENT_USERNAME_NOT_FOUND));
    }*/


    //lấy danh sách parent
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

}
