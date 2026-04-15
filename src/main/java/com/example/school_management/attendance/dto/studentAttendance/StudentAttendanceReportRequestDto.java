package com.example.school_management.attendance.dto.studentAttendance;

// ── REQUEST — parent fetches yearly report ───────────────────────────

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


@Data
public class StudentAttendanceReportRequestDto {

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Roll number is required")
    @Positive(message = "Roll number must be a positive integer")
    private Integer rollNumber;

    @NotBlank(message = "Academic year is required (e.g., 2025-26)")
    private String academicYear;
}