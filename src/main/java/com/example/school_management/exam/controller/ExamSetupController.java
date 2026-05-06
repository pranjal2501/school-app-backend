package com.example.school_management.exam.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.exam.entity.EvaluationComponent;
import com.example.school_management.exam.entity.ExamType;
import com.example.school_management.exam.service.ExamSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exams/setup")
@RequiredArgsConstructor
public class ExamSetupController {

    private final ExamSetupService examSetupService;

    // --- Exam Types ---
    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExamType>> createExamType(@RequestBody Map<String, String> request) {
        ExamType created = examSetupService.createExamType(request.get("name"));
        return ResponseEntity.ok(ApiResponse.ok("Exam Type created", created));
    }

    @GetMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ExamType>>> getExamTypes() {
        return ResponseEntity.ok(ApiResponse.ok("Exam Types fetched", examSetupService.getAllActiveExamTypes()));
    }

    // --- Evaluation Components ---
    @PostMapping("/components")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EvaluationComponent>> createComponent(@RequestBody Map<String, String> request) {
        EvaluationComponent created = examSetupService.createComponent(request.get("name"));
        return ResponseEntity.ok(ApiResponse.ok("Component created", created));
    }

    @GetMapping("/components")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<EvaluationComponent>>> getComponents() {
        return ResponseEntity.ok(ApiResponse.ok("Components fetched", examSetupService.getAllActiveComponents()));
    }
}
