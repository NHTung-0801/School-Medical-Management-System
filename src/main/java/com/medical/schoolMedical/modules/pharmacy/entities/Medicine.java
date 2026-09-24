package com.medical.schoolMedical.modules.pharmacy.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL)
    private List<MedicineUsed> medicineUsed;

    @NotBlank(message = "Tên thuốc không được để trống")
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotBlank(message = "Đơn vị không được để trống")
    @Column(name = "unit", length = 10, nullable = false)
    private String unit;

    @Min(value = 0, message = "Số lượng trong kho không được âm")
    @Column(name = "quantity_in_stock", nullable = false)
    private int quantityInStock;

    @Column(name = "entry_date", nullable = false)
    @CreationTimestamp
    private LocalDate entryDate;

    @NotNull(message = "Vui lòng chọn hạn sử dụng")
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
}
