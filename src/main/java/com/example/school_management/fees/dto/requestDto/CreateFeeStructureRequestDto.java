package com.example.school_management.fees.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateFeeStructureRequestDto {

    @NotBlank
    private String standard;

    @NotBlank
    private String academicYear;

    @NotNull
    @Positive
    private Double totalAmount;

    private String description;

    @NotEmpty
    private List<FeeComponentRequestDto> components;

    @NotEmpty
    private List<CreateInstallmentPlanRequestDto> installmentPlans;
}