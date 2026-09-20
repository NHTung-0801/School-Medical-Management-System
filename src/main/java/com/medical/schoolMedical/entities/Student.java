package com.medical.schoolMedical.entities;

import com.medical.schoolMedical.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "parent_id", nullable = false)
    @ToString.Exclude
    private Parent parent;

    @Column(name = "fullname", length = 50,nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "address", columnDefinition = "TEXT", nullable = false)
    private String address;

    @Column(name = "class_name",length = 10, nullable = false)
    private String className;

    @Column(name = "createAt", nullable = false)
    @CreationTimestamp
    private LocalDate createAt;

}

