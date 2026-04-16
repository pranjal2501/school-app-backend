package com.example.school_management.assignment.controller;

import com.example.school_management.academics.dto.response.AssignmentResponseDto;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.assignment.service.AssignmentService;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
@Validated
@CrossOrigin("*")
public class AssignmentController {

    private final AssignmentService assignmentService;

    @GetMapping("/my-allocations")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<TeacherClassDropdownDto>>> getMyAllocations() {
        List<TeacherClassDropdownDto> response = assignmentService.getMyAllocations();
        return ResponseEntity.ok(ApiResponse.ok("Teacher allocations fetched and grouped successfully", response));
    }

    // 2. Teacher submits the assignment here
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentResponseDto>> createAssignment(
            @Valid @RequestBody AssignmentRequestDto request) {

        AssignmentResponseDto response = assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Assignment successfully uploaded and deployed.", response));
    }

    // 3. Students/Parents view it here
    @GetMapping("/classes/{classId}")
    @PreAuthorize("isAuthenticated()") // Or specifically Student/Parent roles
    public ResponseEntity<ApiResponse<List<AssignmentResponseDto>>> getClassAssignments(
            @PathVariable Long classId) {

        List<AssignmentResponseDto> response = assignmentService.getAssignmentsForClass(classId);
        return ResponseEntity.ok(ApiResponse.ok("Class assignments fetched successfully.", response));
    }
}