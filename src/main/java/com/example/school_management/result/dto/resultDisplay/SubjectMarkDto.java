package com.example.school_management.result.dto.resultDisplay;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectMarkDto {
    private String subjectName;
    private String subjectCode;

    // max marks from Subject
    private Integer theoryMax;
    private Integer internalMax;
    private Integer practicalMax;
    private Integer projectMax;
    private Integer oralMax;
    private Integer totalMax;
    private Integer passingMarks;

    // obtained marks from StudentSubjectMark
    private Integer theoryObtained;
    private Integer internalObtained;
    private Integer practicalObtained;
    private Integer projectObtained;
    private Integer oralObtained;
    private Integer totalObtained;

    private Boolean isPassed;
}