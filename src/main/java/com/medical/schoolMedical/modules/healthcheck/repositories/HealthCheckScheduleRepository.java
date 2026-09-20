package com.medical.schoolMedical.modules.healthcheck.repositories;

import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthCheckScheduleRepository extends JpaRepository<HealthCheckSchedule, Long> {
    Page<HealthCheckSchedule> findAll(Pageable pageable);
    Page<HealthCheckSchedule> findBySentToParent(boolean sentToParent, Pageable pageable);

    // Câu lệnh thuần SQL để lấy thống kê số lượng lịch khám sức khỏe theo tháng trong một năm
    @Query(value = """
        SELECT m.month AS month, 
               COALESCE(COUNT(h.health_check_schedule_id), 0) AS total
        FROM (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION
                     SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION
                     SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) AS m
        LEFT JOIN health_check_schedule h 
          ON MONTH(h.sent_date) = m.month AND YEAR(h.sent_date) = :year
        GROUP BY m.month
        ORDER BY m.month
    """, nativeQuery = true)
    List<Object[]> getMonthlyHealthCheckStats(@Param("year") int year);
}
