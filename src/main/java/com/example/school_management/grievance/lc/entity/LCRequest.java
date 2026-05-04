package com.example.school_management.grievance.lc.entity;

import com.example.school_management.common.enums.GrievanceStatus;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lc_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LCRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private Parent requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private GrievanceStatus status = GrievanceStatus.PENDING;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "leaving_date")
    private LocalDate leavingDate;

    @Column(name = "conduct", length = 100)
    private String conduct;

    @Column(name = "last_attended_year", length = 20)
    private String lastAttendedYear;

    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    private void prePersist() {
        this.requestedAt = LocalDateTime.now();
    }
}
