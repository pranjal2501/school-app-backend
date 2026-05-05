package com.example.school_management.specialEventFees.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailedFeeResponse {
    private Long id;
    private String eventName;
    private String description;
    private LocalDateTime paymentDeadline;
    private LocalDateTime eventDateTime;
    private String className;
    private Double feeAmountPerStudent;
    private Double totalExpected;
    private Double totalCollected;
    private Double totalPending;
    private Float collectionPercent;
    private Integer totalStudents;
    private Integer totalPaid;
    private Integer totalUnpaid;
    private List<SpecialEventFeePaymentResponseDto> summary;
}
