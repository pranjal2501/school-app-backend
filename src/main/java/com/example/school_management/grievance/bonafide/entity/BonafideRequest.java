package com.example.school_management.grievance.bonafide.entity;

import com.example.school_management.common.enums.BonafideStatus;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bonafide_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonafideRequest {

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
    @Column(nullable = false)
    @Builder.Default
    private BonafideStatus status = BonafideStatus.PENDING;

    @Column(name = "purpose", length = 255)
    private String purpose;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    private void prePersist() {
        this.requestedAt = LocalDateTime.now();
    }
}