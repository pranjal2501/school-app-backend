package com.example.school_management.attendance.dto.studentAttendance;

// ── RESPONSE — mark attendance confirmation ──────────────────────────

import com.example.school_management.common.enums.Attendance.AttendanceType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MarkAttendanceResponseDto {

    private Long id;
    private LocalDate attendanceDate;
    private String academicYear;
    private AttendanceType attendanceType;
    private Long classId;
    private String className;
    private Long markedById;
    private String markedByName;

    private List<Integer> absentRollNumbers;
    private List<Integer> lateRollNumbers;
    private List<Integer> halfDayRollNumbers;

    private Integer totalStudents;
    private Integer totalPresent;
    private Integer totalAbsent;
    private Integer totalLate;
    private Integer totalHalfDay;
}
