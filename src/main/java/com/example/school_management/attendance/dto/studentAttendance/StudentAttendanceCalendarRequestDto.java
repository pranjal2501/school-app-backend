package com.example.school_management.attendance.dto.studentAttendance;

// ── REQUEST — parent fetches child's calendar ────────────────────────

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class StudentAttendanceCalendarRequestDto {

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Roll number is required")
    private Integer rollNumber;

    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    @NotNull(message = "Month is required")
    private Integer month;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotBlank(message = "Academic year is required")
    private String academicYear;
}
