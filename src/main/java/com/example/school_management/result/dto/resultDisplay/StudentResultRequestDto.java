package com.example.school_management.result.dto.resultDisplay;

import com.example.school_management.common.enums.ExamTerm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentResultRequestDto {
    @NotBlank
    private String registrationId;

    @NotNull
    private ExamTerm examTerm;

    @NotBlank
    private String academicYear;
}
