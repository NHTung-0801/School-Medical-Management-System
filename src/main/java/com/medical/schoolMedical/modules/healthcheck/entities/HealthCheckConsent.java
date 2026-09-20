package com.medical.schoolMedical.modules.healthcheck.entities;

import com.medical.schoolMedical.entities.Parent;
import com.medical.schoolMedical.entities.Student;
import com.medical.schoolMedical.enums.ConsentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"student", "parent"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "health_check_consent")
public class HealthCheckConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_consent_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    @ToString.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "parent_id", nullable = false)
    @ToString.Exclude
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_check_schedule_id", referencedColumnName = "health_check_schedule_id", nullable = false)
    @ToString.Exclude
    private HealthCheckSchedule schedule;

    @ToString.Exclude
    @OneToOne(mappedBy = "healthCheckConsent", fetch = FetchType.LAZY)
    private HealthCheckRecord healthCheckRecord;

    @Builder.Default
    @Column(name = "is_checked_health", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean checkedHealth = false;

    @Column(name = "consent_status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'UNCONFIRMED'")
    @Enumerated(EnumType.STRING)
    private ConsentStatus status;
}
