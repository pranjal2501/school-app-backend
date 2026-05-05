package com.example.school_management.fees.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.common.enums.fees.FeeAllocationStatus;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "fee_allocations",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"student_id", "academic_year"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeAllocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_plan_id")
    private InstallmentPlan selectedPlan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private FeeAllocationStatus status = FeeAllocationStatus.PLAN_NOT_SELECTED;

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Column(nullable = false)
    private Double totalAmount;

    @OneToMany(
            mappedBy = "feeAllocation",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<FeeInstallment> installments = new ArrayList<>();
}