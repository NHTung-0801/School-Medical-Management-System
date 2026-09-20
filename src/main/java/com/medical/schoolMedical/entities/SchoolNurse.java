package com.medical.schoolMedical.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "school_nurses")
public class SchoolNurse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_nurse_id")
    private long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "nurse", cascade = CascadeType.ALL)
    private List<HealthCheckSchedule> healthCheckSchedules;

    @Column(name = "fullname", length = 50)
    private String fullName;

    @Column(name = "years_of_experience")
    private int experience;
}
