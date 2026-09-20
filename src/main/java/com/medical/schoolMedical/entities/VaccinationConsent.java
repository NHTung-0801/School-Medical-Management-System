package com.medical.schoolMedical.entities;

import com.medical.schoolMedical.enums.ConsentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Timer;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vaccination_consent")
public class VaccinationConsent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_consent_id")
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
    @JoinColumn(name = "schedule_id", referencedColumnName = "schedule_id", nullable = false)
    @ToString.Exclude
    private VaccinationSchedule schedule;

    @Column(name = "consent_status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'UNCONFIRMED'")
    @Enumerated(EnumType.STRING)
    private ConsentStatus status;

    @Builder.Default
    @Column(name = "vaccinated",columnDefinition = "TINYINT DEFAULT 0",nullable = false)
    private boolean vaccinated = false;

    @ToString.Exclude
    @OneToOne(mappedBy = "vaccinationConsent", fetch = FetchType.LAZY)
    private VaccinationRecord vaccinationRecord;

}
