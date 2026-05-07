package com.example.school_management.result.entity;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.exam.entity.EvaluationComponent;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_subject_marks", uniqueConstraints = {
        // A student can only have ONE mark entry for a specific component in a specific exam for a subject
        @UniqueConstraint(columnNames = {
                "student_id",
                "class_subject_teacher_id",
                "exam_type_id",
                "evaluation_component_id"
        })
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_subject_teacher_id", nullable = false)
    private ClassSubjectTeacher allocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_type_id", nullable = false)
    private ExamType examType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_component_id", nullable = false)
    private EvaluationComponent evaluationComponent;

    @Min(0)
    @Column(name = "obtained_marks", nullable = false)
    private Integer obtainedMarks;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}