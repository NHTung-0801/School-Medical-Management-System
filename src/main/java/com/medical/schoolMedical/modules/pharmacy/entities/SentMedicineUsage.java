package com.medical.schoolMedical.modules.pharmacy.entities;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sent_medicine_usage")
public class SentMedicineUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_medicine_id", referencedColumnName = "sent_medicine_id", nullable = false)
    private SentMedicine sentMedicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "used_by_nurses", referencedColumnName = "school_nurse_id", nullable = false)
    private SchoolNurse schoolNurse;

    @Column(name = "usage_time", nullable = false)
    @CreationTimestamp
    private LocalDate usageTime;

    @Column(name = "medicine_name", nullable = false)
    private String medicineName;

    @Column(name = "dosage ", nullable = false)
    private String dosage;

    @Column(name = "notes ", columnDefinition = "TEXT")
    private String notes;
}
