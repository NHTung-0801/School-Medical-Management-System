package com.medical.schoolMedical.modules.user_management.repositories;

import com.medical.schoolMedical.modules.user_management.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByUser_Id(Long userId);

    Optional<Parent> findByUser_Username(String username);
}
