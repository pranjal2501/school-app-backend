package com.example.school_management.fees.dto.requestDto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class InstallmentScheduleRequestDto {

    @NotNull
    @Positive
    private Integer installmentNumber;

    @NotNull
    @Positive
    private Double amount;

    @NotNull
    @Future
    private LocalDate dueDate;

    private String remarks;
}