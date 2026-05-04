package com.example.school_management.complaint.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.complaint.dto.*;
import com.example.school_management.complaint.service.ComplaintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // 1. Raise Complaint (Returns a list since multiple students can be selected)
    @PostMapping("/raise")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> raiseComplaint(
            @Valid @RequestBody ComplaintRequestDto request) {

        List<ComplaintResponseDto> response = complaintService.raiseComplaint(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Complaints registered successfully", response));
    }

    // 2. Resolve/Update Complaint
    @PatchMapping("/{ticketNumber}/status")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateStatus(
            @PathVariable String ticketNumber,
            @Valid @RequestBody ComplaintStatusUpdateDto request) {

        ComplaintResponseDto response = complaintService.updateComplaintStatus(ticketNumber, request);
        return ResponseEntity.ok(ApiResponse.ok("Complaint status updated", response));
    }

    // 3. Fetch complaints raised BY the logged-in teacher
    @GetMapping("/my-raised")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getMyRaisedComplaints() {
        List<ComplaintResponseDto> response = complaintService.getMyRaisedComplaints();
        return ResponseEntity.ok(ApiResponse.ok("Your raised complaints fetched successfully", response));
    }

    // 4. Fetch complaints raised AGAINST the teacher's class (for resolution)
    @GetMapping("/my-class")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getComplaintsForMyClass() {
        List<ComplaintResponseDto> response = complaintService.getComplaintsForMyClass();
        return ResponseEntity.ok(ApiResponse.ok("Class complaints fetched successfully", response));
    }

    // 5. Toggle Parent Visibility
    @PatchMapping("/{ticketNumber}/visibility")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<ComplaintResponseDto>> updateVisibility(
            @PathVariable String ticketNumber,
            @Valid @RequestBody VisibilityUpdateDto request) {

        ComplaintResponseDto response = complaintService.updateComplaintVisibility(ticketNumber, request);
        return ResponseEntity.ok(ApiResponse.ok("Parent visibility updated", response));
    }

    // 6. Fetch complaints by Class ID
    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getComplaintsByClassId(
            @PathVariable Long classId) {
        List<ComplaintResponseDto> response = complaintService.getComplaintsByClassId(classId);
        return ResponseEntity.ok(ApiResponse.ok("Complaints for class " + classId + " fetched successfully", response));
    }

    @GetMapping("/student/{regId}")
    @PreAuthorize("hasAnyRole('PARENT')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> getStudentComplaints(
            @PathVariable String regId) {

        List<ComplaintResponseDto> response = complaintService.getComplaintsForParent(regId);
        return ResponseEntity.ok(ApiResponse.ok("Complaints fetched successfully.", response));
    }

    @PostMapping("/parent/raise")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> raiseComplaintByParent(
            @RequestBody @Valid RaiseComplaintForEmployeeRequestDto request){
        List<ComplaintResponseDto> response = complaintService.raiseComplaintForEmployee(request);

        return ResponseEntity.ok(ApiResponse.ok("Complaints registered successfully", response));
    }

    @GetMapping("/admin/get-complaints")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ComplaintResponseDto>>> recieverAdminComplaint(){

        List<ComplaintResponseDto> response= complaintService.getRecieverAdminComplaint();
        return ResponseEntity.ok(ApiResponse.ok("Complaints Fetched Succesfully", response));
    }
}