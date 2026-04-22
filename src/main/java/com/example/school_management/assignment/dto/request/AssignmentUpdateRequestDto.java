package com.example.school_management.assignment.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssignmentUpdateRequestDto {
    @NotBlank(message = "Assignment title is required")
    private String title;

    private String description;

    @NotBlank(message = "Document URL is compulsorily required")
    private String documentUrl;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}