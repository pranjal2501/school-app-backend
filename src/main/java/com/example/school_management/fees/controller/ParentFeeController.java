package com.example.school_management.fees.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.fees.dto.requestDto.SelectInstallmentPlanRequestDto;
import com.example.school_management.fees.dto.responseDto.FeeAllocationResponseDto;
import com.example.school_management.fees.dto.responseDto.ParentFeeResponseDto;
import com.example.school_management.fees.service.FeeAllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parent/fees")
@RequiredArgsConstructor
public class ParentFeeController {

    private final FeeAllocationService feeAllocationService;

    @GetMapping("/my-child/{regId}")
    public ResponseEntity<ApiResponse<ParentFeeResponseDto>> getParentFeeView(@PathVariable String regId) {
        return ResponseEntity.ok(ApiResponse.ok("Details for parents pov fetched", feeAllocationService.getParentFeeView(regId)));
    }

    @PostMapping("/my-child/{regId}/select-plan")
    public ResponseEntity<ApiResponse<FeeAllocationResponseDto>> selectPlan(
            @PathVariable String regId,
            @RequestBody @Valid SelectInstallmentPlanRequestDto request) throws BadRequestException {

        // Get allocation id from regId first
        FeeAllocationResponseDto allocation = feeAllocationService
                .getFeeAllocationForStudent(regId, feeAllocationService.getCurrentAcademicYear());

        return ResponseEntity.ok(ApiResponse.ok("Fee plan selected", feeAllocationService.selectPlan(allocation.getId(), request)));
    }
}
