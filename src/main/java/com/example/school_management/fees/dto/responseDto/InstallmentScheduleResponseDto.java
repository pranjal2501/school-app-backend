package com.example.school_management.fees.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentScheduleResponseDto {
    private Long id;
    private Integer installmentNumber;
    private Double amount;
    private LocalDate dueDate;
    private String remarks;
}