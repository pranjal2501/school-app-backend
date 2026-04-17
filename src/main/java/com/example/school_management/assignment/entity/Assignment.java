package com.example.school_management.assignment.entity;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.common.enums.AssignmentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // This will hold the AWS S3 / Cloudinary URL for the PDF/Word doc
    @Column(name = "document_url")
    private String documentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false)
    private AssignmentType assignmentType;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "total_marks")
    private Integer totalMarks;

    // 🔒 THE VAULT: This single relationship locks the assignment to a valid Teacher+Class+Subject combo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_subject_teacher_id", nullable = false)
    private ClassSubjectTeacher allocation;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}