package com.medical.schoolMedical.modules.user_management.repositories;

import com.medical.schoolMedical.modules.user_management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
    List<User> findByIsDeletedFalse();
    boolean existsByEmail(String email);
    User findByEmail(String email);
}
