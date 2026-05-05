package com.example.school_management.fees.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentPlanResponseDto {
    private Long id;
    private String planName;
    private Integer numberOfInstallments;
    private String description;
    private List<InstallmentScheduleResponseDto> schedules;
}