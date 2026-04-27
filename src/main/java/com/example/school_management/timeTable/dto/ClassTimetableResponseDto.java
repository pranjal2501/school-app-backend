package com.example.school_management.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassTimetableResponseDto {
    private String className;
    private String academicYear;
    private Map<DayOfWeek, List<TimetableSlotResponseDto>> schedule;
}
