package com.medical.schoolMedical.modules.pharmacy.entities;

import com.medical.schoolMedical.entities.Parent;
import com.medical.schoolMedical.entities.Student;
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
@Table(name = "sent_medicine")
public class SentMedicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sent_medicine_id")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "parent_id", nullable = false)
    private Parent parent;

    @Column(name = "medicine_list", nullable = false)
    private String medicineList;

    @Column(name = "usage_instructions", columnDefinition = "TEXT", nullable = false)
    private String usageInstructions;

    @Column(name = "sent_date", nullable = false)
    @CreationTimestamp
    private LocalDate sentDate;
}
