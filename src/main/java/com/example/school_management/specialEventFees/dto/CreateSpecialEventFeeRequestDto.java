package com.example.school_management.specialEventFees.dto;

import com.example.school_management.common.enums.FeeType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateSpecialEventFeeRequestDto {
    @NotBlank
    private String eventName;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private Double feeAmount;

    @NotNull
    private FeeType feeType;

    @NotNull
    @Future
    private LocalDateTime paymentDeadline;

    @NotNull
    @Future
    private LocalDateTime eventDateTime;

    @NotNull
    private Long classId;
}