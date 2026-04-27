package com.example.school_management.timeTable.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.timeTable.dto.ClassTimetableResponseDto;
import com.example.school_management.timeTable.dto.TimetableConfigRequestDto;
import com.example.school_management.timeTable.dto.TimetableGenerationResponseDto;
import com.example.school_management.timeTable.service.TimetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
@Slf4j
public class TimeTableController {

    private final TimetableService timetableService;

    // admin saves config
    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> saveConfig(
            @Valid @RequestBody TimetableConfigRequestDto request) {
        timetableService.saveConfig(request);
        return ResponseEntity.ok(ApiResponse.ok("Timetable config saved successfully", null));
    }

    // admin generates timetable
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TimetableGenerationResponseDto>> generate(
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.ok("Timetable generated successfully",
                timetableService.generateTimetable(academicYear)));
    }

    // fetch timetable for specific class
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<ApiResponse<ClassTimetableResponseDto>> getClassTimetable(
            @PathVariable Long classId,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.ok("Timetable fetched successfully",
                timetableService.getClassTimetable(classId, academicYear)));
    }

    // fetch timetable for specific teacher
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ClassTimetableResponseDto>> getTeacherTimetable(
            @PathVariable Long teacherId,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.ok("Timetable fetched successfully",
                timetableService.getTeacherTimetable(teacherId, academicYear)));
    }

    @GetMapping("/class/{classId}/download")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'PARENT')")
    public ResponseEntity<byte[]> downloadClassTimetable(
            @PathVariable Long classId,
            @RequestParam String academicYear) {
        try {
            ClassTimetableResponseDto timetable = timetableService.getClassTimetable(classId, academicYear);
            byte[] excel = timetableService.generateTimetableExcel(timetable);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Timetable_"
                                    + timetable.getClassName().replace(" ", "_") + ".xlsx")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel);

        } catch (IOException e) {
            log.error("Failed to download timetable for class: {}. Error: {}", classId, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
