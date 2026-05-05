package com.example.school_management.specialEventFees.dto;

import com.example.school_management.common.enums.FeeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTotalSummaryResponse {
    private Double totalExpected;
    private Double totalCollected;
    private Float collectionRate;
    private List<DetailedFeeResponse> feeRequests;
}
