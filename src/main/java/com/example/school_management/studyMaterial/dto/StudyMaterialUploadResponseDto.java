package com.example.school_management.studyMaterial.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyMaterialUploadResponseDto {

    private Long StudyMaterialId;

    private Long classSubjectTeacherEntityId;

    private String schoolClass;

    private String subject;

    private String teacher;

    private List<String> urls;
}
