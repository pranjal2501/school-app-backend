package com.example.school_management.exam.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marking_schemes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_type_id", "standard", "subject_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MarkingScheme extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_type_id")
    private ExamType examType;

    @Column(nullable = false, length = 10)
    private String standard; // e.g., "7"

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    // Cascade ensures details are saved/deleted with the parent scheme
    @OneToMany(mappedBy = "markingScheme", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MarkingSchemeDetail> details = new ArrayList<>();

    public void addDetail(MarkingSchemeDetail detail) {
        details.add(detail);
        detail.setMarkingScheme(this);
    }
}