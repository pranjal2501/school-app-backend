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
public class FeeAllocationResponseDto {
    private Long id;
    private String studentName;
    private String registrationId;
    private String standard;
    private String academicYear;
    private Double totalAmount;
    private FeeAllocationStatus status;
    private String selectedPlan;
    private List<FeeInstallmentResponseDto> installments;
    private Double totalPaid;
    private Double totalPending;
}