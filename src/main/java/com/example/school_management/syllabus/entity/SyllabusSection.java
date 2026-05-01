package com.example.school_management.syllabus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabus_sections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "syllabus_id", nullable = false)
    private Syllabus syllabus;

    // 🚀 A section can have Sub-Sections...
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SyllabusSubSection> subSections = new ArrayList<>();

    // 🚀 ...OR it can have direct Chapters!
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SyllabusChapter> directChapters = new ArrayList<>();
}