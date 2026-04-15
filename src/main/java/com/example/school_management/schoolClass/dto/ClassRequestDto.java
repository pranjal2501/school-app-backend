package com.example.school_management.schoolClass.dto;

import com.example.school_management.common.ValidationMessages;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassRequestDto {
    @NotNull(message = ValidationMessages.STANDARD_REQUIRED)
    private String standard;

    @NotNull(message = ValidationMessages.DIVISION_REQUIRED)
    private String division;

    @Min(0)
    private Integer totalStudents;

    @Min(0)
    private Integer boys;

    @Min(0)
    private Integer girls;
}
