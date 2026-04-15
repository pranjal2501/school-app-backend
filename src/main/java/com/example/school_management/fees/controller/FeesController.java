package com.example.school_management.fees.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.fees.service.FeeService;
import com.example.school_management.fees.dto.FeesRequestDto;
import com.example.school_management.fees.dto.FeesResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
public class FeesController {

    private final FeeService feesService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeesResponseDto>> createFees(
            @Valid @RequestBody FeesRequestDto requestDTO) {

        FeesResponseDto response = feesService.createFees(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Fees created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FeesResponseDto>>> getAllFees() {

        List<FeesResponseDto> response = feesService.getAllFees();

        return ResponseEntity.ok(
                ApiResponse.ok("Fees fetched successfully", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeesResponseDto>> getFeesById(
            @PathVariable Long id) {

        FeesResponseDto response = feesService.getFeesById(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Fees fetched successfully", response)
        );
    }

    @GetMapping("/std/{std}")
    public ResponseEntity<ApiResponse<List<FeesResponseDto>>> getFeesByStd(
            @PathVariable String std) {

        List<FeesResponseDto> response = feesService.getFeesByStd(std);

        return ResponseEntity.ok(
                ApiResponse.ok("Fees by standard fetched successfully", response)
        );
    }

    @GetMapping("/std/{std}/year/{academicYear}")
    public ResponseEntity<ApiResponse<List<FeesResponseDto>>> getFeesByStdAndYear(
            @PathVariable String std,
            @PathVariable String academicYear) {

        List<FeesResponseDto> response =
                feesService.getFeesByStdAndYear(std, academicYear);

        return ResponseEntity.ok(
                ApiResponse.ok("Fees by standard and year fetched successfully", response)
        );
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<FeesResponseDto>> markAsPaid(
            @PathVariable Long id) {

        FeesResponseDto response = feesService.markAsPaid(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Fees marked as paid successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFees(
            @PathVariable Long id) {

        feesService.deleteFees(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Fees deleted successfully", null)
        );
    }

//    @GetMapping("/standards")
//    public ResponseEntity<ApiResponse<List<String>>> getStandards() {
//
//        List<String> standards = Arrays.stream(Standard.values())
//                .map(Standard::getValue)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(
//                ApiResponse.ok("Standards fetched successfully", standards)
//        );
//    }
}