package com.example.school_management.result.dto.resultDisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentResultDto {
    private String componentName;
    private Integer maxMarks;
    private Integer passingMarks;
    private Integer obtainedMarks;
    private Boolean isPassed;
}