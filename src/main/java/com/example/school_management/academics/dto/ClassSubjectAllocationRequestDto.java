package com.example.school_management.academics.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Payload for assigning an EXISTING subject to a specific class
@Data
public class ClassSubjectAllocationRequestDto {
    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;
}