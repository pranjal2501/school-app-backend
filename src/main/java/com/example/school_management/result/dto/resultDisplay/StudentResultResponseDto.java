package com.example.school_management.result.dto.resultDisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResultResponseDto {
    private String letterheadUrl;

    // Student info
    private String studentName;
    private String registrationId;
    private String className;
    private Integer rollNumber;

    // Exam Info
    private String examTypeName;
    private String academicYear;

    // Per subject breakdown
    private List<SubjectMarkDto> subjects;

    // Totals
    private Integer grandTotalObtained;
    private Integer grandTotalMax;
    private Float percentage;
    private Boolean isOverallPassed;
}