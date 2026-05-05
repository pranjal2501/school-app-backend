package com.example.school_management.fees.entity;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.fees.FeeInstallmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "fee_installments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeInstallment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_allocation_id", nullable = false)
    private FeeAllocation feeAllocation;

    @Column(nullable = false)
    private Integer installmentNumber;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FeeInstallmentStatus status = FeeInstallmentStatus.PENDING;

    @Column
    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_admin_id")
    private Admin paidBy;

    @Column
    private String remarks;
}