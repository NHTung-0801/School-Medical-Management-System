package com.medical.schoolMedical.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;


import java.time.LocalDate;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "medicine")
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medicine_id")
    private Long id;

    // Quan hệ với bảng trung gian
    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL)
    private List<MedicineUsed> medicineUsed;

    @Column(name = "name", length = 100,nullable = false)
    private String name;

    @Column(name = "unit", length = 10,nullable = false)
    private String unit;

    @Column(name = "quantity_in_stock", nullable = false)
    private int quantityInStock;

    @Column(name = "entry_date", nullable = false)
    @CreationTimestamp
    private LocalDate entryDate;

    @Column(name = "expiry_date",nullable = false)
    private LocalDate expiryDate;
}

