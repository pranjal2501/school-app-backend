package com.example.school_management.fees.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectInstallmentPlanRequestDto {

    @NotNull
    private Long planId;
}
