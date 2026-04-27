package com.example.school_management.timeTable.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
public class TimetableConfigRequestDto {
    @NotNull
    private List<DayOfWeek> workingDays;

    @NotNull
    private LocalTime schoolStartTime;

    @NotNull
    private LocalTime schoolEndTime;

    @NotNull
    private Integer periodDurationMinutes;

    @NotNull
    private Integer breakAfterPeriod;

    @NotNull
    private Integer breakDurationMinutes;

    @NotBlank
    private String academicYear;
}
