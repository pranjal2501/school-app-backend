package com.example.school_management.result.dto.resultDisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectMarkDto {
    private String subjectName;
    private String subjectCode;
    private String examTypeName;

    // Dynamic list replacing hardcoded theory/practical fields
    private List<ComponentResultDto> components;

    private Integer totalMax;
    private Integer totalPassingMarks;
    private Integer totalObtained;

    private Boolean isPassed;
}