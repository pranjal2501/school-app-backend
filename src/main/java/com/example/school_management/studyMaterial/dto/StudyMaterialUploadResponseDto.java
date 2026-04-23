package com.example.school_management.studyMaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMaterialUploadResponseDto {

    private Long studyMaterialId;
    private Set<Long> classSubjectTeacherIds;
    private List<String> schoolClasses; // e.g., ["10-A", "10-B"]
    private String subject;
    private String teacher;
    private String description;
    private Boolean isVisibleToParent;
    private List<String> urls;
}