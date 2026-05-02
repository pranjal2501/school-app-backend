package com.example.school_management.syllabus.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabus_chapters")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusChapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer orderIndex;

    // A chapter belongs to EITHER a Section OR a SubSection
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    private SyllabusSection section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_section_id")
    private SyllabusSubSection subSection;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SyllabusTopic> topics = new ArrayList<>();
}