package com.example.school_management.studyMaterial.entity;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyMaterial extends BaseEntity {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "study_material_class_allocations",
            joinColumns = @JoinColumn(name = "study_material_id"),
            inverseJoinColumns = @JoinColumn(name = "class_subject_teacher_id")
    )
    @Builder.Default
    private Set<ClassSubjectTeacher> classTeachers = new HashSet<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVisibleToParent = true;

    @ElementCollection
    @CollectionTable(name = "study_material_urls",
            joinColumns = @JoinColumn(name = "study_material_id"))
    @Column(name = "url", length = 500)
    @Builder.Default
    private List<String> urls = new ArrayList<>();
}