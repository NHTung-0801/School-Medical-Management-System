package com.medical.schoolMedical.modules.pharmacy.entities;

import com.medical.schoolMedical.modules.medical_event.entities.MedicalEvent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supply_used")
public class SupplyUsed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supply_used_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_supply_id", nullable = false)
    private MedicalSupply medicalSupply;

    @ManyToOne
    @JoinColumn(name = "medical_event_id", nullable = false)
    private MedicalEvent medicalEvent;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}
