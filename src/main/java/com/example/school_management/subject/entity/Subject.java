package com.example.school_management.subject.entity;

import com.example.school_management.common.enums.SubjectType;
import com.example.school_management.exam.entity.MarkingScheme;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "standard")
    private String standard;

    @Column(name = "subject_code", unique = true)
    private String subjectCode;

    @Min(1)
    @Column(name = "weekly_hours", nullable = false)
    private Integer weeklyHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject_type", nullable = false)
    private SubjectType subjectType;

    @Builder.Default
    @Column(name = "theory_marks")
    private Integer theoryMarks = 0;

    @Builder.Default
    @Column(name = "internal_marks")
    private Integer internalMarks = 0;

    @Builder.Default
    @Column(name = "practical_marks")
    private Integer practicalMarks = 0;

    @Builder.Default
    @Column(name = "project_marks")
    private Integer projectMarks = 0;

    @Builder.Default
    @Column(name = "oral_marks")
    private Integer oralMarks = 0;

    @Builder.Default
    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks = 0;

    @Builder.Default
    @Column(name = "passing_marks", nullable = false)
    private Integer passingMarks = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_subject_id")
    private Subject parentSubject;

    @OneToMany(mappedBy = "parentSubject", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Subject> childSubjects = new ArrayList<>();

    @Column(name = "is_aggregate_only", nullable = false)
    @Builder.Default
    private boolean isAggregateOnly = false;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MarkingScheme> markingSchemes = new ArrayList<>();

    public void addMarkingScheme(MarkingScheme scheme) {
        markingSchemes.add(scheme);
        scheme.setSubject(this);
    }

    @PrePersist
    protected void generateSubjectCode() {
        // Only generate if not already provided
        if (this.subjectCode == null || this.subjectCode.trim().isEmpty()) {

            // 1. Get first 3 letters of the subject (e.g., "Mathematics" -> "MAT", "IT" -> "IT")
            String prefix = (this.subjectName != null && this.subjectName.length() >= 3)
                    ? this.subjectName.substring(0, 3).toUpperCase()
                    : this.subjectName != null ? this.subjectName.toUpperCase() : null;

            // Sanitize: Remove spaces or special characters just in case
            prefix = prefix != null ? prefix.replaceAll("[^A-Z0-9]", "") : null;

            // 2. Get Standard Name (e.g., "STD_10")
            String std = this.standard != null ? this.standard : "1";

            // 3. Generate a 4-character unique alphanumeric string to prevent DB Unique Constraint crashes
            String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

            // 4. Combine them: e.g., "MAT-STD_10-F3A1"
            this.subjectCode = String.format("%s-%s-%s", prefix, std, uniqueSuffix);
        }
    }
}