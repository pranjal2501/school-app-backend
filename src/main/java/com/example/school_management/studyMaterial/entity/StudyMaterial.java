package com.example.school_management.studyMaterial.entity;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudyMaterial extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "class_subject_teacher_id", nullable = false)
    private ClassSubjectTeacher classTeacher;


    @ElementCollection
    @CollectionTable(name = "study_material_urls",
            joinColumns = @JoinColumn(name = "study_material_id"))
    @Column(name = "url", length = 500)
    @Builder.Default
    private List<String> urls= new ArrayList<>();
}
