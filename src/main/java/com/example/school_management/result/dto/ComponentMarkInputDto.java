package com.example.school_management.result.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class ComponentMarkInputDto {
    @NotNull(message = "Evaluation Component ID is required.")
    private Long evaluationComponentId;

    @NotNull(message = "Obtained marks are required.")
    @PositiveOrZero(message = "Marks cannot be negative.")
    private Integer obtainedMarks;
}