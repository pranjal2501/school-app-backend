package com.example.school_management.specialEventFees.mapper;

import com.example.school_management.specialEventFees.dto.SpecialEventFeePaymentResponseDto;
import com.example.school_management.specialEventFees.dto.SpecialEventFeeResponseDto;
import com.example.school_management.specialEventFees.entity.SpecialEventFee;
import com.example.school_management.specialEventFees.entity.SpecialEventFeePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SpecialEventFeeMapper {

    public SpecialEventFeeResponseDto toResponseDto(
            SpecialEventFee fee,
            int totalPaid,
            int totalUnpaid) {

        return SpecialEventFeeResponseDto.builder()
                .id(fee.getId())
                .eventName(fee.getEventName())
                .description(fee.getDescription())
                .feeAmount(fee.getFeeAmount())
                .feeType(fee.getFeeType())
                .paymentDeadline(fee.getPaymentDeadline())
                .eventDateTime(fee.getEventDateTime())
                .className(fee.getSchoolClass().getClassName())
                .createdByTeacher(fee.getCreatedByTeacher().getFirstName()
                        + " " + fee.getCreatedByTeacher().getLastName())
                .createdAt(fee.getCreatedAt())
                .totalStudents(totalPaid + totalUnpaid)
                .totalPaid(totalPaid)
                .totalUnpaid(totalUnpaid)
                .build();
    }

    public SpecialEventFeePaymentResponseDto toPaymentResponseDto(
            SpecialEventFeePayment payment) {

        return SpecialEventFeePaymentResponseDto.builder()
                .id(payment.getId())
                .studentName(payment.getStudent().getFirstName()
                        + " " + payment.getStudent().getLastName())
                .parentName(payment.getParent().getFatherFirstName()
                        + " " + payment.getParent().getFatherLastName())
                .feeAmount(payment.getFeeAmount())
                .feePaid(payment.getFeePaid())
                .feePaidAt(payment.getFeePaidAt())
                .deadlineExpired(payment.getSpecialEventFee().getPaymentDeadline() != null
                        && LocalDateTime.now().isAfter(
                        payment.getSpecialEventFee().getPaymentDeadline()))
                .build();
    }
}
