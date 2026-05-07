package com.example.school_management.result.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class StudentMarkEntryDto {
    @NotNull(message = "Student ID is required.")
    private Long studentId;

    @NotEmpty(message = "Component marks cannot be empty.")
    @Valid
    private List<ComponentMarkInputDto> componentMarks;
}