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
/**
    Extract the 'Who' in the Controller. Enforce the 'What they can do' in the Service.
    Keep Spring Security imports out of your Service layer whenever humanly possible.
*/
    private final ComplaintService complaintService;

    /**
     * @AuthenticationPrincipal injects the currently authenticated UserDetails object directly.
     * This avoids tightly coupling our Service layer to Spring Security Contexts.
     */
    @PostMapping("/raise")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> raiseComplaint(
            @Valid @RequestBody ComplaintRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        ComplaintResponseDto response = complaintService.raiseComplaint(request, userDetails.getUsername(), isAdmin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Complaint registered successfully", response));
    }

    /**
     * @PatchMapping is the REST standard for PARTIAL updates (like changing just a status),
     * whereas @PutMapping is meant for replacing an entire resource.
     */
    @PatchMapping("/{ticketNumber}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateStatus(
            @PathVariable String ticketNumber,
            @Valid @RequestBody ComplaintStatusUpdateDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        ComplaintResponseDto response = complaintService.updateComplaintStatus(ticketNumber, request, userDetails.getUsername(), isAdmin);

        return ResponseEntity.ok(ApiResponse.ok("Complaint status updated", response));
    }
}