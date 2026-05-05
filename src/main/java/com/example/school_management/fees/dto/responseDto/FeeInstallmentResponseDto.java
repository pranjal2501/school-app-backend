package com.example.school_management.fees.dto.responseDto;

import com.example.school_management.common.enums.fees.FeeInstallmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeInstallmentResponseDto {
    private Long id;
    private Integer installmentNumber;
    private Double amount;
    private LocalDate dueDate;
    private FeeInstallmentStatus status;
    private LocalDateTime paidAt;
    private String remarks;
    private Boolean isOverdue;
}
