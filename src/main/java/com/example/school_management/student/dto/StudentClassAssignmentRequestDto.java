package com.example.school_management.student.dto;

import com.example.school_management.common.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StudentClassAssignmentRequestDto {

    private List<Long> studentId;

    @NotNull(message = ValidationMessages.DIVISION_REQUIRED)
    private String division;
}
