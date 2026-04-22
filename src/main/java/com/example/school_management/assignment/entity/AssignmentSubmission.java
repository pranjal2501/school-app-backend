package com.example.school_management.assignment.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "assignment_submissions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"assignment_id", "student_id"})
})
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class AssignmentSubmission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "is_submitted", nullable = false)
    private boolean isSubmitted;
}