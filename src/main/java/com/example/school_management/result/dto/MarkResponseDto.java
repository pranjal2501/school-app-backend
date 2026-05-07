package com.example.school_management.result.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class MarkResponseDto {
    private Long studentId;
    private String studentName;
    private Integer rollNumber;

    private Long examTypeId;
    private String examTypeName;

    private List<ComponentMarkResponseDto> componentMarks;

    private Integer totalObtained;
    private Integer totalMaxMarks;
    private boolean isPassed;
}