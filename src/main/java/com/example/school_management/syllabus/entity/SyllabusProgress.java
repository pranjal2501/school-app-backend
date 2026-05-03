package com.example.school_management.syllabus.entity;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "syllabus_progress", indexes = {
        // Fast lookups to see what is complete for a specific class/subject
        @Index(name = "idx_progress_allocation", columnList = "allocation_id"),
        @Index(name = "idx_allocation_node", columnList = "allocation_id, node_path", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which class/subject/teacher combination is this progress for?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocation_id", nullable = false)
    private ClassSubjectTeacher allocation;

    // The Materialized Path (e.g., "SEC_5", "SEC_5/SUB_12", "SEC_5/SUB_12/CHAP_42")
    // This string uniquely identifies the node and its place in the hierarchy.
    @Column(name = "node_path", nullable = false)
    private String nodePath;

    // Is it complete?
    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    // When was it marked complete?
    @CreationTimestamp
    @Column(name = "completed_at", updatable = false)
    private LocalDateTime completedAt;

    // Utility method to find the parent path
    public String getParentPath() {
        if (nodePath == null || !nodePath.contains("/")) return null;
        return nodePath.substring(0, nodePath.lastIndexOf('/'));
    }
}