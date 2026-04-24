package com.example.school_management.result.dto.resultDisplay;

import com.example.school_management.common.enums.ExamTerm;
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

    // student info
    private String studentName;
    private String registrationId;
    private String className;
    private Integer rollNo;
    private ExamTerm examTerm;
    private String academicYear;

    // per subject breakdown
    private List<SubjectMarkDto> subjectMarks;

    // totals
    private Integer grandTotalObtained;
    private Integer grandTotalMax;
    private Float percentage;
    private Boolean isOverallPassed;
}

