package com.example.school_management.syllabus.entity;

import com.example.school_management.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "syllabuses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"standard_name", "subject_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Syllabus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "standard_name", nullable = false)
    private String standard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @OneToMany(mappedBy = "syllabus", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SyllabusSection> sections = new ArrayList<>();
}