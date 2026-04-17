package com.example.school_management.assignment.dto.request;

import com.example.school_management.common.enums.AssignmentType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentRequestDto {

    @NotBlank(message = "Assignment title is required")
    private String title;

    private String description;

    // Optional because some assignments (like "Read Chapter 4") don't need a file
    private String documentUrl;

    @NotNull(message = "Assignment type is required")
    private AssignmentType assignmentType;

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future") // 400 Bad Request if past
    private LocalDateTime dueDate;

    private Integer totalMarks;

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;
}