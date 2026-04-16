package com.example.school_management.complaint.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.complaint.service.ComplaintService;
import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.complaint.dto.ComplaintResponseDto;
import com.example.school_management.complaint.dto.ComplaintStatusUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;

    @PostMapping("/raise")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> raiseComplaint(
            @Valid @RequestBody ComplaintRequestDto request) {
        ComplaintResponseDto response = complaintService.raiseComplaint(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Complaint registered successfully", response));
    }

    @PatchMapping("/{ticketNumber}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateStatus(
            @PathVariable String ticketNumber,
            @Valid @RequestBody ComplaintStatusUpdateDto request) {

        ComplaintResponseDto response = complaintService.updateComplaintStatus(ticketNumber, request);

        return ResponseEntity.ok(ApiResponse.ok("Complaint status updated", response));
    }
}