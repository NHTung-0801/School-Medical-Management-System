package com.medical.schoolMedical.modules.pharmacy.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "medical_supply")
public class MedicalSupply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medical_supply_id")
    private Long id;

    // Quan hệ với bảng trung gian
    @OneToMany(mappedBy = "medicalSupply", cascade = CascadeType.ALL)
    private List<SupplyUsed> supplyUsed;

    @NotBlank(message = "Tên vật tư không được để trống")
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Min(value = 0, message = "Số lượng trong kho không được âm")
    @Column(name = "quantity_in_stock", nullable = false)
    private int quantityInStock;

    @Column(name = "entry_date", nullable = false)
    @CreationTimestamp
    private LocalDate entryDate;
}
