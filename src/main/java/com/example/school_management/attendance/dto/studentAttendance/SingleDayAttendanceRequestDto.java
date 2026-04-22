package com.example.school_management.attendance.dto.studentAttendance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SingleDayAttendanceRequestDto {

    @NotNull
    private Long classId;

    @NotNull
    private LocalDate date;
}