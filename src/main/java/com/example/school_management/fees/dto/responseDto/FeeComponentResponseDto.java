package com.example.school_management.fees.dto.responseDto;

import com.example.school_management.common.enums.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeComponentResponseDto {
    private Long id;
    private FeeType feeType;
    private String feeTypeDisplayName;
    private Double amount;
    private String remarks;
}