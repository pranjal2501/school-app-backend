package com.example.school_management.teacher.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeSummaryDto {
    private String parentName;
    private String studentName;
    private Double feeAmount;
    private LocalDateTime paymentDeadline;
    private Boolean feePaid;
    private LocalDateTime feePaidAt;
    private Boolean deadlineExpired;
}