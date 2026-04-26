package com.example.school_management.attendance.dto.studentAttendance;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class SingleDayAttendanceRequestDto {

    @NotNull(message = "Class ID cannot be null")
    private Long classId;

    @NotNull(message = "Date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
}
