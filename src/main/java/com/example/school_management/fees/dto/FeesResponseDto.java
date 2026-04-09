package com.example.school_management.fees.dto;

import com.example.school_management.common.enums.FeeType;
import com.example.school_management.common.enums.FineType;
import com.example.school_management.common.enums.PaymentCycle;
import com.example.school_management.common.enums.Standard;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeesResponseDto {

    private Long id;
    private Standard std;
    private List<FeeItemDTO> feeItems;
    private PaymentCycle paymentCycle;
    private String academicYear;
    private BigDecimal totalAmount;
    private FineType fineType;
    private BigDecimal fineAmount;
    private boolean paid;
    private Instant createdAt;
    private Instant paidAt;
    private Instant updatedAt;

    @Data
    @Builder
    public static class FeeItemDTO {
        private FeeType feeType;
        private String feeTypeCode;
        private BigDecimal amount;
    }
}