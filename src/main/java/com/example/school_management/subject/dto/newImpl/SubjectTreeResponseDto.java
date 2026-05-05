package com.example.school_management.subject.dto.newImpl;

import com.example.school_management.teacher.dto.response.TeacherResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectTreeResponseDto {
    private Long id;
    private String subjectName;
    private String subjectCode;
    private boolean isAggregateOnly;
    private Integer totalMarks;

    // Recursive self-reference for the UI Tree
    private List<SubjectTreeResponseDto> childSubjects;

    private TeacherResponseDto allocatedTeacher;

    private List<SubjectExamResponseDto> exams;

    private Long allocatedTeacherId;
    private String allocatedTeacherName;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubjectExamResponseDto {
        private Long markingSchemeId;
        private Long examTypeId;
        private String examTypeName;
        private List<ComponentMarkResponseDto> components;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentMarkResponseDto {
        private Long evaluationComponentId;
        private String componentName;
        private Integer maxMarks;
        private Integer passingMarks;
    }
}