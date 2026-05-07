package com.example.school_management.result.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.result.dto.MarkResponseDto;
import com.example.school_management.result.dto.MarkUploadRequestDto;
import com.example.school_management.result.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marks")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class MarkController {

    private final MarkService markService;

    // 1. Bulk Upload / Update Marks
    @PostMapping("/upload")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> uploadMarks(
            @Valid @RequestBody MarkUploadRequestDto request) {

        List<MarkResponseDto> response = markService.uploadOrUpdateMarks(request);

        return ResponseEntity.status(HttpStatus.OK) // Using 200 OK because it could be an update or create
                .body(ApiResponse.ok("Marks successfully saved/updated.", response));
    }

    // 2. Fetch the marks grid for a specific allocation and Exam Type
    @GetMapping("/allocations/{allocationId}/exam-types/{examTypeId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<MarkResponseDto>>> getMarks(
            @PathVariable Long allocationId,
            @PathVariable Long examTypeId) {

        List<MarkResponseDto> response = markService.getMarksForAllocation(allocationId, examTypeId);
        return ResponseEntity.ok(ApiResponse.ok("Marks fetched successfully.", response));
    }
}