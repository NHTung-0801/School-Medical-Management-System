package com.medical.schoolMedical.modules.healthcheck.entities;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Health_check_schedule")
public class HealthCheckSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_schedule_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_nurse_id", referencedColumnName = "school_nurse_id", nullable = false)
    @ToString.Exclude
    private SchoolNurse nurse;

    @ToString.Exclude
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<HealthCheckConsent> healthCheckConsent;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "className", columnDefinition = "TEXT")
    private int className;

    @Column(name = "check_date", nullable = false)
    private LocalDateTime checkDate;

    @Column(name = "sent_date")
    private LocalDate sentDate;

    @Column(name = "create_date", nullable = false)
    @CreationTimestamp
    private LocalDate createDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_sent_to_parent", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean sentToParent = false;
}
