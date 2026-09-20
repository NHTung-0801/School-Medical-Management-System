package com.medical.schoolMedical.modules.medical_event.repositories;

import com.medical.schoolMedical.modules.medical_event.entities.MedicalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalEventRepository extends JpaRepository<MedicalEvent, Long> {
    List<MedicalEvent> findAll();
    List<MedicalEvent> findByStudentId(Long studentId);
    List<MedicalEvent> findBySchoolNurseId(Long nurseId);

    @Query("SELECT e FROM MedicalEvent e " +
            "LEFT JOIN FETCH e.medicineUsed " +
            "LEFT JOIN FETCH e.supplyUsed " +
            "WHERE e.id = :id")
    Optional<MedicalEvent> findByIdWithDetails(@Param("id") Long id);

    // Câu lệnh thuần SQL để lấy thống kê số lượng sự kiện y tế theo tháng trong một năm
    @Query(value = """
        SELECT m.month AS month, 
               COALESCE(COUNT(me.medical_event_id), 0) AS total
        FROM (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION
                     SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION
                     SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) AS m
        LEFT JOIN medical_event me 
          ON MONTH(me.event_time) = m.month AND YEAR(me.event_time) = :year
        GROUP BY m.month
        ORDER BY m.month
    """, nativeQuery = true)
    List<Object[]> getMonthlyMedicalEventStats(@Param("year") int year);
}
