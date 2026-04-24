package com.example.school_management.studyMaterial.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParentSubjectMaterialResponseDto {

    // --- Subject Details ---
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    private String subjectType; // COMPULSORY / OPTIONAL
    private Integer totalMarks;

    // --- Teacher Details ---
    private TeacherInfoDto teacher;

    // --- Study Materials ---
    private List<ParentStudyMaterialDto> materials;

    @Data
    @Builder
    public static class TeacherInfoDto {
        private String name;
        private String degree;
        private String email;
        private String mobile;
    }

    @Data
    @Builder
    public static class ParentStudyMaterialDto {
        private Long id;
        private String description;
        private List<String> urls;
    }
}