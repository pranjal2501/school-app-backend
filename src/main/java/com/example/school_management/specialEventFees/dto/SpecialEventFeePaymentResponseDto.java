package com.example.school_management.specialEventFees.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialEventFeePaymentResponseDto {
    private Long id;
    private String studentName;
    private String parentName;
    private Double feeAmount;
    private Boolean feePaid;
    private LocalDateTime feePaidAt;
    private Boolean deadlineExpired;
}
