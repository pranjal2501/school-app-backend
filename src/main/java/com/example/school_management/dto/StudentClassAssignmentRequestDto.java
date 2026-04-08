package com.example.school_management.dto;

import com.example.school_management.constants.ValidationMessages;
import com.example.school_management.enums.Division;
import com.example.school_management.enums.Standard;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentClassAssignmentRequestDto {

    @NotNull(message = ValidationMessages.ROLL_NUMBER_REQUIRED)
    @Min(value = 1, message = ValidationMessages.ROLL_NUMBER_MIN)
    private Integer rollNumber;

    @NotNull(message = ValidationMessages.STANDARD_REQUIRED)
    private Standard standard;

    @NotNull(message = ValidationMessages.DIVISION_REQUIRED)
    private Division division;
}
