package com.example.school_management.fees.dto.requestDto;

import com.example.school_management.common.enums.FeeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FeeComponentRequestDto {

    @NotNull
    private FeeType feeType;

    @NotNull
    @Positive
    private Double amount;

    private String remarks;
}