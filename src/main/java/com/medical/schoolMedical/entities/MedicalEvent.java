package com.medical.schoolMedical.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medical_event")
public class MedicalEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by_nurse_id", referencedColumnName = "school_nurse_id", nullable = false)
    private SchoolNurse schoolNurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "event_time", nullable = false)
    @CreationTimestamp
    private Timestamp eventTime;

    @Column(name = "location", length = 150, nullable = false)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "initial_treatment", columnDefinition = "TEXT")
    private String initial_treatment;

    @Column(name = "final_treatment", columnDefinition = "TEXT")
    private String final_treatment;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;



    @OneToMany(mappedBy = "medicalEvent", cascade = CascadeType.ALL)
    private Set<MedicineUsed> medicineUsed = new HashSet<>();

    @OneToMany(mappedBy = "medicalEvent", cascade = CascadeType.ALL)
    private Set<SupplyUsed> supplyUsed = new HashSet<>();
}

