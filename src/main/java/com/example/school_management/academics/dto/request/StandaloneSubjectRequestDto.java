package com.example.school_management.academics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StandaloneSubjectRequestDto {
    @NotBlank
    private String subjectName;
    @NotBlank private String subjectCode;
    @NotBlank private String standard; // e.g., "STD_10"
    @NotBlank private String subjectType; // e.g., "OPTIONAL"
    @NotNull private Integer theoryMarks;
    @NotNull private Integer practicalMarks;
    @NotNull
    private Integer totalMarks;
    @NotNull private Integer passingMarks;
}