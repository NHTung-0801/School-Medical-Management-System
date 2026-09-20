package com.medical.schoolMedical.modules.consultation.entities;

import com.medical.schoolMedical.modules.user_management.entities.SchoolNurse;
import com.medical.schoolMedical.modules.user_management.entities.Student;
import com.medical.schoolMedical.enums.ConsentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consultation_appointment")
public class ConsultationAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_appointment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    @ToString.Exclude
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_nurse_id", referencedColumnName = "school_nurse_id", nullable = false)
    @ToString.Exclude
    private SchoolNurse schoolNurse;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "createdAt")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "sent_date")
    private LocalDate sentDate;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'UNCONFIRMED'")
    @Enumerated(EnumType.STRING)
    private ConsentStatus status;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(name = "is_sent_to_parent", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean sentToParent = false;

    @Builder.Default
    @Column(name = "is_viewed_by_parent", columnDefinition = "TINYINT DEFAULT 0", nullable = false)
    private boolean viewedByParent = false;
}
