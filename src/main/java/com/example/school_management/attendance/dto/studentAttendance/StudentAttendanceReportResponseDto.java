package com.example.school_management.attendance.dto.studentAttendance;

// ── RESPONSE — yearly summary report ────────────────────────────────

import lombok.Data;

import java.util.List;

@Data
public class StudentAttendanceReportResponseDto {

    private String studentName;
    private Integer rollNumber;
    private String className;
    private String academicYear;

    // counts
    private Integer totalWorkingDays;
    private Long daysConducted;
    private Long totalPresent;
    private Long totalAbsent;
    private Long totalLate;
    private Long totalHalfDay;
    private Long totalHolidays;

    // percentage
    private Float attendancePercentage;
    private Boolean isShortage;

    // monthly breakdown — useful for a bar chart if UI needs it
    private List<MonthlyBreakdown> monthlyBreakdown;

    public static class MonthlyBreakdown {
        private String month;
        private Integer present;
        private Integer absent;
        private Float percentage;
    }
}

