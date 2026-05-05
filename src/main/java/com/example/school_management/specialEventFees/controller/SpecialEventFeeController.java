package com.example.school_management.specialEventFees.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.specialEventFees.dto.*;
import com.example.school_management.specialEventFees.service.SpecialEventFeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/special-event-fees")
@RequiredArgsConstructor
public class SpecialEventFeeController {

    private final SpecialEventFeeService feeService;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SpecialEventFeeResponseDto>> createFee(
            @Valid @RequestBody CreateSpecialEventFeeRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok("Special event fee created successfully",
                        feeService.createFee(request)));
    }

    @GetMapping("/my-fees")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SpecialEventFeeResponseDto>>> getMyFees() {
        return ResponseEntity.ok(ApiResponse.ok("Fees fetched successfully",
                feeService.getMyFees()));
    }

    @GetMapping("/{feeId}/summary")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<List<SpecialEventFeePaymentResponseDto>>> getFeeSummary(
            @PathVariable Long feeId) {
        return ResponseEntity.ok(ApiResponse.ok("Fee summary fetched successfully",
                feeService.getFeeSummary(feeId)));
    }

    @PatchMapping("/payments/{paymentId}/mark-paid")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<String>> markFeePaid(
            @PathVariable Long paymentId) {
        return ResponseEntity.ok(ApiResponse.ok("Fee marked as paid",
                feeService.markFeePaid(paymentId)));
    }

    @GetMapping("/all-special-event")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<SpecialEventParentResponseDto>>> getAllEventForClass(
            @RequestParam String RegId
    )
    {
        List<SpecialEventParentResponseDto> response= feeService.getAllEventsForChild(RegId);
        return ResponseEntity.ok(ApiResponse.ok("Special Events fetched", response));
    }

    @GetMapping("/detailed-info")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<TeacherTotalSummaryResponse>> getDetailedInfoGraphics()
    {
        TeacherTotalSummaryResponse response= feeService.getDetailedInfoGraphics();

        return ResponseEntity.ok(ApiResponse.ok("Details Fetched", response));
    }

}