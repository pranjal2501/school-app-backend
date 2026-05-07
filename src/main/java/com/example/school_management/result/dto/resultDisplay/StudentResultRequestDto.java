package com.example.school_management.result.dto.resultDisplay;

import lombok.Data;

@Data
public class StudentResultRequestDto {
    private String registrationId;
    private Long examTypeId;
    private String academicYear;
}