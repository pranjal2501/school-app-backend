package com.example.school_management.attendance.controller;

import com.example.school_management.attendance.dto.studentAttendance.*;
import com.example.school_management.attendance.service.StudentAttendanceService;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/student/attendance")
@RequiredArgsConstructor
public class StudentAttendanceController {

    private final StudentAttendanceService attendanceService;

    //Mark attendance for a whole class.
    @PostMapping("/mark")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<MarkAttendanceResponseDto>> markAttendance(
            @Valid @RequestBody MarkAttendanceRequestDto request,
            @RequestParam Long teacherId) throws AccessDeniedException {

        MarkAttendanceResponseDto response = attendanceService.markAttendance(request, teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Attendance marked successfully", response));
    }

    //Get monthly calendar view for a specific student (Parent Dashboard).
    @GetMapping("/monthly-calendar")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<StudentMonthlyCalendarResponseDto>> getMonthlyCalendar(
            @Valid @RequestBody StudentAttendanceCalendarRequestDto request) throws AccessDeniedException {

        return ResponseEntity.ok(ApiResponse.ok("Record fetched successfully",attendanceService.getMonthlyCalendar(request)));
    }


    //Get yearly attendance report for a student.
    @GetMapping("/yearly-report")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<StudentAttendanceReportResponseDto>> getYearlyReport(
            @Valid @RequestBody StudentAttendanceReportRequestDto request) throws AccessDeniedException {

        return ResponseEntity.ok(ApiResponse.ok("Yearwise report",attendanceService.getYearlyReport(request)));
    }
}