package com.example.school_management.exam.entity;

import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "marking_scheme_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MarkingSchemeDetail extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "marking_scheme_id")
    private MarkingScheme markingScheme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_component_id")
    private EvaluationComponent evaluationComponent;

    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks;
}