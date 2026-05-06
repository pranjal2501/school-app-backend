package com.example.school_management.exam.entity;

import com.example.school_management.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "evaluation_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class EvaluationComponent extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name; // e.g., "Theory", "Oral", "Practical"

    @Builder.Default
    private boolean isActive = true;
}
