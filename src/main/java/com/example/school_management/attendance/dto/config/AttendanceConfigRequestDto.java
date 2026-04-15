package com.example.school_management.attendance.dto.config;

import com.example.school_management.common.enums.Attendance.AttendanceType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AttendanceConfigRequestDto {

    @NotBlank(message = "Academic year is required")
    private String academicYear;

    @NotNull(message = "Attendance type is required")
    private AttendanceType attendanceType;

    @Min(value = 1, message = "Student working days must be at least 1")
    private Integer studentWorkingDays;

    @DecimalMin(value = "0.0", message = "Min attendance cannot be negative")
    @DecimalMax(value = "100.0", message = "Min attendance cannot exceed 100")
    private Float studentMinAttendancePct;

    @Min(value = 1, message = "Teacher working days must be at least 1")
    private Integer teacherWorkingDays;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Float teacherMinAttendancePct;

    private Boolean allowLateMarks;

    @Min(value = 0)
    private Integer lateMarksWindow;

    private Boolean notifyParentOnAbsent;

    @Min(value = 1)
    private Integer absentNotifyThreshold;

    @Min(value = 0)
    private Integer minLeaveNoticeDays;
}
