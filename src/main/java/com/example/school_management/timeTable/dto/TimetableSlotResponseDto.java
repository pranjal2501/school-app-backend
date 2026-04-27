package com.example.school_management.timeTable.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimetableSlotResponseDto {
    private String className;
    private String subjectName;
    private String teacherName;
    private DayOfWeek dayOfWeek;
    private Integer periodNumber;
    private LocalTime startTime;
    private LocalTime endTime;
}