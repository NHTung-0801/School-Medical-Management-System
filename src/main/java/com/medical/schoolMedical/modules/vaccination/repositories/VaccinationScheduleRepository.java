package com.medical.schoolMedical.modules.vaccination.repositories;

import com.medical.schoolMedical.modules.vaccination.entities.VaccinationSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VaccinationScheduleRepository extends JpaRepository<VaccinationSchedule, Long> {
    Page<VaccinationSchedule> findBySentToParent(boolean sentToParent, Pageable pageable);

    // Câu lệnh thuần SQL để lấy thống kê số lượng lịch tiêm chủng theo tháng trong một năm
    @Query(value = """
        SELECT m.month AS month, 
               COALESCE(COUNT(v.schedule_id), 0) AS total
        FROM (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION
                     SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION
                     SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) AS m
        LEFT JOIN vaccination_schedule v 
          ON MONTH(v.sent_date) = m.month AND YEAR(v.sent_date) = :year
        GROUP BY m.month
        ORDER BY m.month
    """, nativeQuery = true)
    List<Object[]> getMonthlyVaccinationStats(@Param("year") int year);
}
