package com.example.school_management.assignment.controller;

import com.example.school_management.assignment.dto.request.AssignmentBulkRequestDto;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.request.AssignmentUpdateRequestDto;
import com.example.school_management.assignment.dto.request.SubmissionBulkRequestDto;
import com.example.school_management.assignment.dto.response.AssignmentResponseDto;
import com.example.school_management.assignment.dto.response.SubmissionResponseDto;
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

//        AssignmentResponseDto response = assignmentService.createAssignment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Assignment successfully uploaded and deployed.", null));
    }

    // 3. Students/Parents view it here
    @GetMapping("/classes/{classId}")
    @PreAuthorize("isAuthenticated()") // Or specifically Student/Parent roles
    public ResponseEntity<ApiResponse<List<AssignmentResponseDto>>> getClassAssignments(
            @PathVariable Long classId) {

        List<AssignmentResponseDto> response = assignmentService.getAssignmentsForClass(classId);
        return ResponseEntity.ok(ApiResponse.ok("Class assignments fetched successfully.", response));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<AssignmentResponseDto>>> createAssignmentsBulk(
            @Valid @RequestBody List<AssignmentBulkRequestDto> requests) {

        List<AssignmentResponseDto> response = assignmentService.createBulkAssignments(requests);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Assignments successfully uploaded and deployed.", response));
    }

    @PostMapping("/{assignmentId}/submissions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SubmissionResponseDto>>> updateSubmissions(
            @PathVariable Long assignmentId,
            @Valid @RequestBody SubmissionBulkRequestDto request) {
        List<SubmissionResponseDto> response = assignmentService.upsertSubmissions(assignmentId, request);
        return ResponseEntity.ok(ApiResponse.ok("Submissions updated successfully.", response));
    }

    @GetMapping("/{assignmentId}/submissions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SubmissionResponseDto>>> getSubmissions(
            @PathVariable Long assignmentId) {
        List<SubmissionResponseDto> response = assignmentService.getSubmissionsForAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.ok("Submissions fetched successfully.", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<AssignmentResponseDto>> updateAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentUpdateRequestDto request) {
        AssignmentResponseDto response = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Assignment updated successfully.", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(ApiResponse.ok("Assignment deleted successfully", null));
    }

    @GetMapping("/my-assignments")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<AssignmentResponseDto>>> getMyAssignments() {
        List<AssignmentResponseDto> response = assignmentService.getMyCreatedAssignments();
        return ResponseEntity.ok(ApiResponse.ok("Teacher assignments fetched successfully.", response));
    }
}