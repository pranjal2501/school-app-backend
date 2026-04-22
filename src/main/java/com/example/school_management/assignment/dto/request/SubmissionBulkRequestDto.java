package com.example.school_management.assignment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SubmissionBulkRequestDto {
    @NotEmpty(message = "Submission list cannot be empty")
    @Valid
    private List<StudentSubmissionDto> submissions;
}