package com.medical.schoolMedical.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medicine_used")
public class MedicineUsed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_used_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne
    @JoinColumn(name = "medical_event_id", nullable = false)
    private MedicalEvent medicalEvent;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "notes",columnDefinition = "TEXT")
    private String notes;


}

