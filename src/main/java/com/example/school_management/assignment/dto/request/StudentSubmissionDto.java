package com.example.school_management.assignment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentSubmissionDto {
    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Submission status is required")
    private Boolean isSubmitted;
}