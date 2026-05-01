package com.example.school_management.result.controller;


import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.dto.adminView.MarkFillingStatusResponseDto;
import com.example.school_management.result.service.AdminResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marks-status")
public class AdminResultController {

    private final AdminResultService adminResultService;


    @GetMapping("/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MarkFillingStatusResponseDto>>> getMarkFillingStatus(
            @PathVariable Long classId,
            @RequestParam ExamTerm examTerm) {
        return ResponseEntity.ok(ApiResponse.ok("Marks filling status fetched",
                adminResultService.getMarkFillingStatus(classId, examTerm)));
    }

    @PostMapping("/notify-pending/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> notifyPendingTeachers(
            @PathVariable Long classId,
            @RequestParam ExamTerm examTerm) {
        adminResultService.notifyPendingTeachers(classId, examTerm);
        return ResponseEntity.ok(ApiResponse.ok("Pending teachers notified successfully", null));
    }


}
