package com.example.school_management.syllabus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabus_sub_sections")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusSubSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SyllabusSection section;

    @OneToMany(mappedBy = "subSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SyllabusChapter> chapters = new ArrayList<>();
}