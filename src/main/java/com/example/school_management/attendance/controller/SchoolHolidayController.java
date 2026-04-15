package com.example.school_management.attendance.controller;

import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayRequestDto;
import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayResponseDto;
import com.example.school_management.attendance.service.SchoolHolidayService;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/holidays")
@RequiredArgsConstructor
public class SchoolHolidayController {

    private final SchoolHolidayService holidayService;


    //Add a new holiday to the school calendar.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SchoolHolidayResponseDto>>> addHolidays(
            @Valid @RequestBody List<SchoolHolidayRequestDto> request) throws AccessDeniedException {

        List<SchoolHolidayResponseDto> response = holidayService.addHolidays(request);
        return ResponseEntity.ok(ApiResponse.ok("Holidays added successfully", response));
    }


    //Get all holidays for a specific academic year.
    @GetMapping("/year/{academicYear}")
    public ResponseEntity<ApiResponse<List<SchoolHolidayResponseDto>>> getHolidaysByYear(
            @PathVariable String academicYear) {

        List<SchoolHolidayResponseDto> response = holidayService.getHolidaysByYear(academicYear);
        return ResponseEntity.ok(ApiResponse.ok("Holidays fetched successfully", response));
    }


    //Delete a holiday by ID.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteHoliday(@PathVariable Long id) throws AccessDeniedException {
        holidayService.deleteHoliday(id);
        return ResponseEntity.ok(ApiResponse.ok("Holiday deleted successfully", null));
    }
}
