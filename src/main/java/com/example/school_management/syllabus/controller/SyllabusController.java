package com.example.school_management.syllabus.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.syllabus.dto.SyllabusDtos.SyllabusPayload;
import com.example.school_management.syllabus.service.SyllabusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/syllabus")
@RequiredArgsConstructor
public class SyllabusController {

    private final SyllabusService syllabusService;

    @GetMapping("/standard/{standard}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT','TEACHER')")
    public ResponseEntity<ApiResponse<SyllabusPayload>> getSyllabus(
            @PathVariable String standard,
            @PathVariable Long subjectId) {
        try {
            SyllabusPayload payload = syllabusService.getSyllabus(standard, subjectId);
            return ResponseEntity.ok(ApiResponse.ok("Syllabus fetched successfully", payload));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/admin/standard/{standard}/subject/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> editSyllabus(
            @PathVariable String standard,
            @PathVariable Long subjectId,
            @RequestBody SyllabusPayload payload) {
        try {
            syllabusService.saveOrUpdateSyllabus(standard, subjectId, payload);
            return ResponseEntity.ok(ApiResponse.ok("Syllabus successfully saved/updated.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to update syllabus: " + e.getMessage()));
        }
    }

    @GetMapping("/teacher/allocation/{allocationId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SyllabusPayload>> getSyllabusByAllocation(@PathVariable Long allocationId) {
        try {
            SyllabusPayload payload = syllabusService.getSyllabusByAllocation(allocationId);
            return ResponseEntity.ok(ApiResponse.ok("Syllabus fetched successfully", payload));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch syllabus: " + e.getMessage()));
        }
    }
}