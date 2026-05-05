package com.example.school_management.fees.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.fees.dto.requestDto.CreateFeeStructureRequestDto;
import com.example.school_management.fees.dto.requestDto.MarkInstallmentPaidRequestDto;
import com.example.school_management.fees.dto.responseDto.FeeAllocationResponseDto;
import com.example.school_management.fees.dto.responseDto.FeeStructureResponseDto;
import com.example.school_management.fees.service.FeeAllocationService;
import com.example.school_management.fees.service.FeeStructureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/fees")
@RequiredArgsConstructor
public class AdminFeeController {

    private final FeeStructureService feeStructureService;
    private final FeeAllocationService feeAllocationService;

    // ─── Fee Structure ───────────────────────────────────────────

    @PostMapping("/structures")
    public ResponseEntity<ApiResponse<FeeStructureResponseDto>> createFeeStructure(
            @RequestBody @Valid CreateFeeStructureRequestDto request) {
        return ResponseEntity.ok(ApiResponse.ok("Fee structure created successfully",feeStructureService.createFeeStructure(request)));
    }

    @GetMapping("/structures")
    public ResponseEntity<ApiResponse<List<FeeStructureResponseDto>>> getAllFeeStructures() {
        return ResponseEntity.ok(ApiResponse.ok("Fee structures fetched",feeStructureService.getAllFeeStructures()));
    }

    @GetMapping("/structures/{id}")
    public ResponseEntity<ApiResponse<FeeStructureResponseDto>> getFeeStructureById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Fetched by id " + id,feeStructureService.getFeeStructureById(id)));
    }

    @GetMapping("/structures/{standard}/{year}")
    public ResponseEntity<ApiResponse<FeeStructureResponseDto>> getFeeStructureByStandardAndYear(
            @PathVariable String standard,
            @PathVariable String year) {
        return ResponseEntity.ok(ApiResponse.ok("Fee structure fetched for standard " + standard, feeStructureService.getFeeStructureByStandardAndYear(standard, year)));
    }

    @PutMapping("/structures/{id}")
    public ResponseEntity<ApiResponse<FeeStructureResponseDto>> updateFeeStructure(
            @PathVariable Long id,
            @RequestBody @Valid CreateFeeStructureRequestDto request) throws BadRequestException {
        return ResponseEntity.ok(ApiResponse.ok("Fee structure updated for fee id" + id, feeStructureService.updateFeeStructure(id, request)));
    }

    @DeleteMapping("/structures/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeeStructure(@PathVariable Long id) throws BadRequestException {
        feeStructureService.deleteFeeStructure(id);
        return ResponseEntity.ok(ApiResponse.ok("Deleted successfully", null));
    }

    // ─── Fee Allocations ─────────────────────────────────────────

    @GetMapping("/structures/by-standard/{standard}")
    public ResponseEntity<ApiResponse<List<FeeAllocationResponseDto>>> getAllAllocationsForStandard(
            @PathVariable String standard,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.ok("Records fetched", feeAllocationService.getAllAllocationsForStandard(standard, academicYear)));
    }

    @GetMapping("/allocations/student/{regId}")
    public ResponseEntity<ApiResponse<FeeAllocationResponseDto>> getStudentAllocation(
            @PathVariable String regId,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(ApiResponse.ok("Student info fetched", feeAllocationService.getFeeAllocationForStudent(regId, academicYear)));
    }

    // ─── Installments ─────────────────────────────────────────────

    @PatchMapping("/installments/{id}/mark-paid")
    public ResponseEntity<ApiResponse<FeeAllocationResponseDto>> markInstallmentPaid(
            @PathVariable Long id,
            @RequestBody @Valid MarkInstallmentPaidRequestDto request) throws BadRequestException {
        return ResponseEntity.ok(ApiResponse.ok("Installation paid marked successfully", feeAllocationService.markInstallmentPaid(id, request)));
    }
}