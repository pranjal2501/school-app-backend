package com.example.school_management.syllabus.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "syllabus_topics")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusTopic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private SyllabusChapter chapter;
}