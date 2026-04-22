package com.example.school_management.assignment.dto.request;

import com.example.school_management.common.enums.AssignmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AssignmentBulkRequestDto {

    @NotBlank(message = "Assignment title is required")
    private String title;

    private String description;

    @NotBlank(message = "Document URL is compulsorily required")
    private String documentUrl;

    @NotNull(message = "Assignment type is required")
    private AssignmentType assignmentType;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    private Integer totalMarks;

    @NotEmpty(message = "At least one Class ID is required")
    private List<Long> classIds;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;
}