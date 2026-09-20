package com.medical.schoolMedical.modules.healthcheck.entities;

import com.medical.schoolMedical.entities.SchoolNurse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Health_check_record")
public class HealthCheckRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "health_check_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "health_check_consent_id", referencedColumnName = "health_check_consent_id", nullable = false)
    @ToString.Exclude
    private HealthCheckConsent healthCheckConsent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_nurse_id", referencedColumnName = "school_nurse_id", nullable = false)
    @ToString.Exclude
    private SchoolNurse schoolNurse;

    @Column(name = "vision_result", nullable = false)
    private int visionResult;

    @Column(name = "hearing_result", nullable = false)
    private String hearingResult;

    @Column(name = "bloodPressure")
    private String bloodPressure;

    @Column(name = "heartrate")
    private int heartRate;

    @Column(name = "height")
    private double height;

    @Column(name = "weight")
    private double weight;

    @Column(name = "other_result")
    private String otherResult;

    @Column(name = "assessment")
    private String assessment;

    @Column(name = "needs_consultation", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean needsConsultation = false;

    @Column(name = "is_sent_to_parent", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean sentToParent = false;

    @Column(name = "is_viewed_by_parent", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean viewedByParent = false;
}
