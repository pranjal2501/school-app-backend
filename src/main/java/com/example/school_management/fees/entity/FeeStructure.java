package com.example.school_management.fees.entity;

import com.example.school_management.admin.entity.Admin;
import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "fee_structures",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"standard", "academic_year"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeStructure extends BaseEntity {

    @Column(nullable = false, length = 10)
    private String standard;

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Column(nullable = false)
    private Double totalAmount;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    private Admin createdBy;

    @OneToMany(
            mappedBy = "feeStructure",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<FeeComponent> components = new ArrayList<>();

    @OneToMany(
            mappedBy = "feeStructure",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<InstallmentPlan> installmentPlans = new ArrayList<>();
}
