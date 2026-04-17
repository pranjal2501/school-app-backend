package com.example.school_management.result.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentMarkEntryDto {

    @NotNull(message = "Student ID is required.")
    private Long studentId;

    @Min(value = 0, message = "Marks cannot be negative")
    private Integer theoryObtained;

    @Min(value = 0, message = "Marks cannot be negative")
    private Integer internalObtained;

    @Min(value = 0, message = "Marks cannot be negative")
    private Integer practicalObtained;

    @Min(value = 0, message = "Marks cannot be negative")
    private Integer projectObtained;

    @Min(value = 0, message = "Marks cannot be negative")
    private Integer oralObtained;
}