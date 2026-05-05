package com.example.school_management.fees.dto.requestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateInstallmentPlanRequestDto {

    @NotBlank
    private String planName;

    @NotNull
    @Positive
    private Integer numberOfInstallments;

    private String description;

    @NotEmpty
    private List<InstallmentScheduleRequestDto> schedules;
}
