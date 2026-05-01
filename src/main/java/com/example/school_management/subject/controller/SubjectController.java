package com.example.school_management.subject.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.subject.dto.SubjectResponseDto;
import com.example.school_management.subject.service.SubjectService;
import com.example.school_management.subject.service.SubjectServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Validated
public class SubjectController {
    private final SubjectService subjectService;

    @GetMapping("/standard/{standard}")
    public ResponseEntity<ApiResponse<List<SubjectResponseDto>>> getSubjectsForStandard(
            @PathVariable String standard) {

        try {
            List<SubjectResponseDto> subjects = subjectService.getSubjectsByStandard(standard);

            if (subjects.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.ok("No subjects found for standard: " + standard, subjects));
            }

            return ResponseEntity.ok(ApiResponse.ok("Subjects fetched successfully", subjects));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to fetch subjects: " + e.getMessage()));
        }
    }
}