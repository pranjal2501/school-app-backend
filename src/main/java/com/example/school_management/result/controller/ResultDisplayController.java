package com.example.school_management.result.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.dto.resultDisplay.LetterheadRequestDto;
import com.example.school_management.result.dto.resultDisplay.StudentResultRequestDto;
import com.example.school_management.result.dto.resultDisplay.StudentResultResponseDto;
import com.example.school_management.result.dto.resultDisplay.ToggleResultStatus;
import com.example.school_management.result.service.ResultDisplayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/results")
@RequiredArgsConstructor
public class ResultDisplayController {

    private final ResultDisplayService resultService;

    // admin uploads letterhead
    @PostMapping("/letterhead")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> uploadLetterhead(
            @Valid @RequestBody LetterheadRequestDto requestDto) {
        resultService.saveLetterhead(requestDto);
        return ResponseEntity.ok(ApiResponse.ok("Letterhead saved successfully", null));
    }

    // admin toggles result visibility
    @PatchMapping("/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> toggleResultPublish(
            @RequestBody ToggleResultStatus toggleResultStatusRequest) {
        resultService.toggleResultPublish(toggleResultStatusRequest);
        return ResponseEntity.ok(ApiResponse.ok(
                toggleResultStatusRequest.getPublish() ? "Result published successfully" : "Result unpublished successfully", null));
    }

    // fetch result for specific student
    @GetMapping("/student")
    @PreAuthorize("hasAnyRole('ADMIN', 'PARENT', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudentResultResponseDto>> getStudentResult(
            @Valid @RequestBody StudentResultRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok("Result fetched successfully",
                resultService.getStudentResult(request)));
    }
}
