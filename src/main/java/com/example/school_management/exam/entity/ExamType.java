package com.example.school_management.exam.entity;

import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "exam_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class ExamType extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String name; // e.g., "Term 1", "Unit Test"

    @Builder.Default
    private boolean isActive = true;
}