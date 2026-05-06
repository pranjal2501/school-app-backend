package com.example.school_management.subject.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.subject.dto.BulkSubjectCreateRequestDto;
import com.example.school_management.subject.dto.SubjectResponseDto;
import com.example.school_management.subject.dto.newImpl.SubjectRequestDto;
import com.example.school_management.subject.dto.newImpl.SubjectTreeResponseDto;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.subject.service.SubjectService;
import com.example.school_management.subject.service.SubjectServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    // 1. Create Subject (Handles both Aggregate and Standard)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Subject>> createSubject(@Valid @RequestBody SubjectRequestDto request) {
        Subject created = subjectService.createSubject(request);
        return ResponseEntity.ok(ApiResponse.ok("Subject created successfully", created));
    }

    // 2. The API for the Admin UI "Dictionary" Screen (Returns nested tree)
    @GetMapping("/tree/{standard}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRINCIPAL')")
    public ResponseEntity<ApiResponse<List<SubjectTreeResponseDto>>> getSubjectTree(@PathVariable String standard) {
        List<SubjectTreeResponseDto> tree = subjectService.getSubjectTreeByStandard(standard);
        return ResponseEntity.ok(ApiResponse.ok("Subject tree fetched", tree));
    }

    // 3. The API for Teacher Allocation & Marking Schemes (Returns ONLY leaf nodes)
    @GetMapping("/leaf-nodes/{standard}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<Subject>>> getAllocatableSubjects(@PathVariable String standard) {
        List<Subject> leaves = subjectService.getLeafSubjectsForAllocation(standard);
        return ResponseEntity.ok(ApiResponse.ok("Allocatable subjects fetched", leaves));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> bulkCreateSubjects(
            @Valid @RequestBody BulkSubjectCreateRequestDto request) {

        String result = subjectService.bulkCreateSubjects(request);
        return ResponseEntity.ok(ApiResponse.ok("Subjects configured successfully", result));
    }
}