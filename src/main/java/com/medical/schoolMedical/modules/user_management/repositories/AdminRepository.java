package com.medical.schoolMedical.modules.user_management.repositories;

import com.medical.schoolMedical.modules.user_management.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser_Username(String username);
}
