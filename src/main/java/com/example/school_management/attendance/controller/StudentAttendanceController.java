package com.example.school_management.attendance.controller;

import com.example.school_management.attendance.dto.ClassWeeklyStatDto;
import com.example.school_management.attendance.dto.latemark.UpdateLateMarksRequestDto;
import com.example.school_management.attendance.dto.studentAttendance.*;
import com.example.school_management.attendance.service.StudentAttendanceService;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

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

    @PatchMapping("/{attendanceId}/late-marks")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<String>> updateLateMarks(
            @PathVariable Long attendanceId,
            @RequestBody UpdateLateMarksRequestDto request) {
        attendanceService.updateLateMarks(attendanceId, request);
        return ResponseEntity.ok(ApiResponse.ok("Late marks updated successfully", null));
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

    @GetMapping("/class/{classId}/date/{date}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<MarkAttendanceResponseDto>> getAttendanceByDate(
            @PathVariable Long classId,
            @PathVariable LocalDate date) {

        MarkAttendanceResponseDto response = attendanceService.getClassAttendanceByDate(classId, date);
        return ResponseEntity.ok(ApiResponse.ok("Record fetched", response));
    }

    // Update attendance for a whole class
    @PutMapping("/update")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<MarkAttendanceResponseDto>> updateAttendance(
            @Valid @RequestBody MarkAttendanceRequestDto request,
            @RequestParam Long teacherId) {

        MarkAttendanceResponseDto response = attendanceService.updateAttendance(request, teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Attendance updated successfully", response));
    }

    @GetMapping("/class/{classId}/weekly-trends")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ClassWeeklyStatDto>>> getWeeklyTrends(@PathVariable Long classId) {
        List<ClassWeeklyStatDto> response = attendanceService.getWeeklyClassStats(classId);
        return ResponseEntity.ok(ApiResponse.ok("Weekly trends fetched successfully", response));
    }

    @GetMapping("/daily-report")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<SingleDayAttendanceResponseDto>> fetchSingleDayRecord(
            @Valid @ModelAttribute SingleDayAttendanceRequestDto request
    ) throws AccessDeniedException {

        return ResponseEntity.ok(ApiResponse.ok(
                "Attendance for date " + request.getDate() + " fetched successfully",
                attendanceService.getSingleDayReport(request)
        ));
    }

//    @GetMapping("/daily-report")
//    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
//    public ResponseEntity<ApiResponse<SingleDayAttendanceResponseDto>> fetchSingleDayRecord(
//            @RequestBody SingleDayAttendanceRequestDto request
//    ) throws AccessDeniedException {
//        return ResponseEntity.ok(ApiResponse.ok("Attendance for date "+request.getDate()+"Fetched successfully", attendanceService.getSingleDayReport(request)));
//    }

}