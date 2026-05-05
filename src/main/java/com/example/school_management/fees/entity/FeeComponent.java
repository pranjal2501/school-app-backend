package com.example.school_management.fees.entity;

import com.example.school_management.common.enums.FeeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fee_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeeComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;  // your existing enum

    @Column(nullable = false)
    private Double amount;

    @Column
    private String remarks;
}