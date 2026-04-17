package com.example.school_management.result.entity;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_subject_marks", uniqueConstraints = {
        // A student can only have ONE set of marks for a specific subject in a specific exam term
        @UniqueConstraint(columnNames = {"student_id", "class_subject_teacher_id", "exam_term"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSubjectMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Who got the marks?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // 2. The Vault: What class, subject, and teacher does this belong to?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_subject_teacher_id", nullable = false)
    private ClassSubjectTeacher allocation;

    // 3. What exam is this for? (e.g., MID_TERM, FINAL_EXAM, UNIT_TEST_1)
    @Enumerated(EnumType.STRING)
    @Column(name = "exam_term", nullable = false)
    private ExamTerm examTerm;

    @Min(0)
    @Column(name = "theory_obtained")
    private Integer theoryObtained;

    @Min(0)
    @Column(name = "internal_obtained")
    private Integer internalObtained;

    @Min(0)
    @Column(name = "practical_obtained")
    private Integer practicalObtained;

    @Min(0)
    @Column(name = "project_obtained")
    private Integer projectObtained;

    @Min(0)
    @Column(name = "oral_obtained")
    private Integer oralObtained;

    // --- AUTO-CALCULATED FIELDS ---
    @Column(name = "total_obtained", nullable = false)
    private Integer totalObtained;

    @Column(name = "is_passed", nullable = false)
    private boolean isPassed;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- SMART CALCULATION HOOKS ---
    @PrePersist
    @PreUpdate
    public void calculateTotalAndStatus() {
        // 1. Safely sum up all obtained marks (treating nulls as 0)
        int theory = (theoryObtained != null) ? theoryObtained : 0;
        int internal = (internalObtained != null) ? internalObtained : 0;
        int practical = (practicalObtained != null) ? practicalObtained : 0;
        int project = (projectObtained != null) ? projectObtained : 0;
        int oral = (oralObtained != null) ? oralObtained : 0;

        this.totalObtained = theory + internal + practical + project + oral;

        // 2. Automatically determine Pass/Fail status based on the Subject's exact rules
        if (this.allocation != null && this.allocation.getSubject() != null) {
            Integer passingMarksRequired = this.allocation.getSubject().getPassingMarks();
            this.isPassed = this.totalObtained >= (passingMarksRequired != null ? passingMarksRequired : 0);
        } else {
            this.isPassed = false;
        }
    }
}