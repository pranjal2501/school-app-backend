package com.example.school_management.attendance.dto.studentAttendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleDayAttendanceResponseDto {
    private LocalDate date;

    // overall
    private int totalStudents;
    private int totalPresent;
    private int totalAbsent;
    private int totalLate;

    // gender breakdown
    private int totalBoys;
    private int totalGirls;
    private int totalBoysPresent;
    private int totalGirlsPresent;

    private List<StudentAttendanceRecordDto> records;
}
