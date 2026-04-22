package com.example.school_management.assignment.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubmissionResponseDto {
    private Long studentId;
    private String studentName;
    private Integer rollNumber;
    private boolean isSubmitted;
}