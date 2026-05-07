package com.example.school_management.result.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComponentMarkResponseDto {
    private Long markRecordId;
    private Long evaluationComponentId;
    private String componentName;
    private Integer maxMarks;
    private Integer passingMarks;
    private Integer obtainedMarks;
    private boolean isComponentPassed;
}