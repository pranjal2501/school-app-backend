package com.example.school_management.attendance.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class ClassWeeklyStatDto {
    private LocalDate date;
    private String dayOfWeek;
    private int totalStudents;
    private int presentCount;
    private int absentCount;
    private int lateCount;
    private int halfDayCount;
    private double presentPercentage;
}