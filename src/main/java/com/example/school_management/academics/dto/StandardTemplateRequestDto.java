package com.example.school_management.academics.dto;

import com.example.school_management.common.enums.Standard;
import com.example.school_management.common.enums.SubjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

// Payload for bulk template creation (Points 2 & 3)
@Data
public class StandardTemplateRequestDto {

    @NotBlank(message = "Standard is required (e.g., STD_10)")
    private Standard standard;

    @NotNull(message = "You must provide at least one subject.")
    private List<SubjectTemplateDto> subjects;

    @Data
    public static class SubjectTemplateDto {
        @NotBlank private String subjectName;
        @NotBlank private String subjectCode;
        @NotBlank private SubjectType subjectType;
        @NotNull private Integer theoryMarks;
        @NotNull private Integer practicalMarks;
        @NotNull private Integer totalMarks;
        @NotNull private Integer passingMarks;
        @NotNull private Integer internalMarks;
        @NotNull private Integer projectMarks;
        @NotNull private Integer oralMarks;
        @NotNull private Integer weeklyHours;
        @NotNull(message = "A default teacher must be assigned for this subject.")
        private Long defaultTeacherId; // e.g., Mr. Smith's ID
    }
}

