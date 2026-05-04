package com.example.school_management.specialEventFees.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "special_event_fee_payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SpecialEventFeePayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "special_event_fee_id", nullable = false)
    private SpecialEventFee specialEventFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    @Builder.Default
    private Boolean feePaid = false;

    @Column
    private LocalDateTime feePaidAt;

    @Column(nullable = false)
    private Double feeAmount;
}
