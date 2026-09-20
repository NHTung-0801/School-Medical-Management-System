package com.medical.schoolMedical.modules.healthcheck.repositories;

import com.medical.schoolMedical.enums.ConsentStatus;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckConsent;
import com.medical.schoolMedical.modules.healthcheck.entities.HealthCheckSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthCheckConsentRepository extends JpaRepository<HealthCheckConsent, Long> {
    boolean existsByParent_Id(Long parentId);
    boolean existsByParent_IdAndStatus(Long parentId, ConsentStatus status);
    List<HealthCheckConsent> findByStatus(ConsentStatus status);

    // Lấy danh sách consent đồng ý khám và trạng thái khám hay chưa khám tùy ng dùng chọn
    @Query(value = """
    SELECT h FROM HealthCheckConsent h
    WHERE h.schedule = :scheduleId
    AND h.status = :status
    AND h.checkedHealth = :is_checked_health
    ORDER BY h.id ASC
    """)
    Page<HealthCheckConsent> listConsentsByScheduleAndStatusAndCheckState(
            @Param("scheduleId") HealthCheckSchedule scheduleId,
            @Param("status") ConsentStatus status,
            @Param("is_checked_health") boolean is_checked_health,
            Pageable pageable
    );

    // Lấy các consent của parent tương ứng
    Page<HealthCheckConsent> findByParent_User_IdOrderByIdDesc(Long userId, Pageable pageable);

    @Query("SELECT c FROM HealthCheckConsent c JOIN FETCH c.schedule WHERE c.status = :status")
    List<HealthCheckConsent> findByStatusWithSchedule(@Param("status") ConsentStatus status);

    // Lấy các consent tương ứng với bản record là đã có kq khám và cần tạo lịch gửi đến phụ huynh
    @Query("""
        SELECT c FROM HealthCheckConsent c
        LEFT JOIN c.healthCheckRecord r
        WHERE c.schedule = :schedule
          AND c.status = :status
          AND c.checkedHealth = :isCheckedHealth
          AND r.needsConsultation = true
    """)
    Page<HealthCheckConsent> findByScheduleStatusCheckStateWithConsultation(
            @Param("schedule") HealthCheckSchedule schedule,
            @Param("status") ConsentStatus status,
            @Param("isCheckedHealth") boolean isCheckedHealth,
            Pageable pageable
    );
}
