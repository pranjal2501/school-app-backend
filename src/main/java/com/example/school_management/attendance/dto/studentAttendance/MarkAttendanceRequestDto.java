package com.example.school_management.attendance.dto.studentAttendance;

import com.example.school_management.common.enums.attendance.AttendanceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ── REQUEST — teacher marks whole class ──────────────────────────────

@Data
public class MarkAttendanceRequestDto {

    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Attendance date is required")
    @PastOrPresent(message = "Attendance cannot be marked for future dates")
    private LocalDate attendanceDate;

    @NotBlank(message = "Academic year is required (e.g., 2025-26)")
    private String academicYear;

    @NotNull(message = "Attendance type is required")
    private AttendanceType attendanceType = AttendanceType.DAY_WISE;

    private Long subjectId = null;

    @NotNull
    private List<Integer> absentRollNumbers = new ArrayList<>();

    @NotNull
    private List<Integer> lateRollNumbers = new ArrayList<>();

    @NotNull
    private List<Integer> halfDayRollNumbers = new ArrayList<>();
}