package com.medical.schoolMedical.modules.vaccination.entities;

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
@Table(name = "vaccination_record")
public class VaccinationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_record_id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccination_consent_id", referencedColumnName = "vaccination_consent_id", nullable = false)
    @ToString.Exclude
    private VaccinationConsent vaccinationConsent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_nurse_id", referencedColumnName = "school_nurse_id", nullable = false)
    @ToString.Exclude
    private SchoolNurse schoolNurse;

    @Column(name = "post_vaccination_condition", nullable = false)
    private String postVaccinationCondition;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_sent_to_parent", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean sentToParent = false;

    @Column(name = "is_viewed_by_parent ", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean viewedByParent = false;
}
