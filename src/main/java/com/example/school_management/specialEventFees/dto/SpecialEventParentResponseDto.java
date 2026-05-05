package com.example.school_management.specialEventFees.dto;

import com.example.school_management.common.enums.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialEventParentResponseDto {

    private Long id;
    private String eventName;
    private String description;
    private Double feeAmount;
    private FeeType feeType;
    private LocalDateTime paymentDeadline;
    private LocalDateTime eventDateTime;
    private String className;
    private String createdByTeacher;
    private Boolean paymentStatus;
}
