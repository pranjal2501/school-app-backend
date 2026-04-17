package com.example.school_management.result.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarkResponseDto {
    private Long markId;
    private Long studentId;
    private String studentName;
    private Integer rollNumber;

    private Integer theoryObtained;
    private Integer internalObtained;
    private Integer practicalObtained;
    private Integer projectObtained;
    private Integer oralObtained;

    private Integer totalObtained;
    private boolean isPassed;
}