package com.example.school_management.fees.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructureResponseDto {
    private Long id;
    private String standard;
    private String academicYear;
    private Double totalAmount;
    private String description;
    private List<FeeComponentResponseDto> components;
    private List<InstallmentPlanResponseDto> installmentPlans;
    private Instant createdAt;
}