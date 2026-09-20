package com.medical.schoolMedical.modules.user_management.repositories;

import com.medical.schoolMedical.modules.user_management.entities.Parent;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findAll();

    List<Student> findByParent(Parent parent);

    List<Student> findByParent_Id(Long parentId);

    List<Student> findByClassNameStartingWith(String classNamePrefix);

    // danh sách các student được thêm ở tháng và năm chỉ định
    @Query("SELECT s FROM Student s JOIN FETCH s.parent WHERE MONTH(s.createAt) = :month AND YEAR(s.createAt) = :year")
    List<Student> findByMonthAndYear(@Param("month") int month, @Param("year") int year);
}
