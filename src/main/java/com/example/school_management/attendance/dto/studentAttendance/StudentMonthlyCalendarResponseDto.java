package com.example.school_management.attendance.dto.studentAttendance;

// ── RESPONSE — monthly calendar (parent dashboard main screen) ───────

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StudentMonthlyCalendarResponseDto {

    private String studentName;
    private Integer rollNumber;
    private String className;
    private String month;                       // "March 2026"

    // top stat cards in screenshot
    private Integer totalPresent;
    private Integer totalAbsent;
    private Integer totalHolidays;
    private Float attendancePercentage;

    // calendar grid
    private List<DailyRecord> dailyRecords;

    @Data
    public static class DailyRecord {
        private LocalDate date;
        private Integer dayOfMonth;
        private String dayOfWeek;
        private DailyStatus status;
    }

    public enum DailyStatus {
        PRESENT,    // green
        ABSENT,     // red
        LATE,       // yellow
        HALF_DAY,   // different shade
        HOLIDAY,    // orange
        SUNDAY,     // white
        FUTURE      // white
    }
}
