package com.example.school_management.student.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentClassAssignmentRequestDto {

    @NotNull(message = ValidationMessages.STANDARD_REQUIRED)
    private Standard standard;

    @NotNull(message = ValidationMessages.DIVISION_REQUIRED)
    private Division division;
}
