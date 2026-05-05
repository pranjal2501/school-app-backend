package com.example.school_management.subject.dto;

import com.example.school_management.common.enums.SubjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkSubjectCreateRequestDto {

    @NotBlank(message = "Standard is required")
    private String standard;

    @NotEmpty(message = "At least one subject must be provided")
    @Valid
    private List<SubjectNodeDto> subjects;

    @Data
    public static class SubjectNodeDto {
        @NotBlank(message = "Subject name is required")
        private String subjectName;

        @Min(1)
        private Integer weeklyHours;

        private SubjectType subjectType;
        private boolean isAggregateOnly;

        // Nested Children (For Aggregate Folders)
        @Valid
        private List<SubjectNodeDto> childSubjects;

        // Nested Exams (For Leaf Subjects)
        @Valid
        private List<SubjectExamConfigDto> exams;

        private Long defaultTeacherId;
    }

    @Data
    public static class SubjectExamConfigDto {
        @NotNull(message = "Exam Type ID is required")
        private Long examTypeId;

        @NotEmpty(message = "At least one component mark must be defined")
        @Valid
        private List<ComponentMarkDto> components;
    }

    @Data
    public static class ComponentMarkDto {
        @NotNull
        private Long evaluationComponentId;

        @NotNull @Min(1)
        private Integer maxMarks;

        @NotNull @Min(0)
        private Integer passingMarks;
    }
}