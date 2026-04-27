package com.example.school_management.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableGenerationResponseDto {
    private String academicYear;
    private Integer totalSlotsGenerated;
    private List<String> unscheduledSubjects;
}