package com.example.school_management.dto;

import com.example.school_management.constants.ValidationMessages;
import com.example.school_management.enums.Division;
import com.example.school_management.enums.Standard;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassRequestDto {
    @NotNull(message = ValidationMessages.STANDARD_REQUIRED)
    private Standard standard;

    @NotNull(message = ValidationMessages.DIVISION_REQUIRED)
    private Division division;

    @Min(0)
    private Integer totalStudents;

    @Min(0)
    private Integer boys;

    @Min(0)
    private Integer girls;

    private String className;
}
