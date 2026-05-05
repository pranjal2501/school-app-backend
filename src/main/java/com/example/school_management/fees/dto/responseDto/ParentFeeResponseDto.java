package com.example.school_management.fees.dto.responseDto;

import com.example.school_management.common.enums.fees.FeeAllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParentFeeResponseDto {
    private String studentName;
    private String registrationId;
    private String standard;
    private String academicYear;
    private Double totalFee;
    private Double totalPaid;
    private Double totalPending;
    private FeeAllocationStatus status;
    private Boolean planSelected;
    private List<InstallmentPlanResponseDto> availablePlans; // shown only if PLAN_NOT_SELECTED
    private List<FeeInstallmentResponseDto> installments;   // shown after plan selected
    private List<FeeComponentResponseDto> feeBreakdown;     // for pamphlet
}
