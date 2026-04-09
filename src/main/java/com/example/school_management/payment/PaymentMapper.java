package com.example.school_management.payment;

import com.example.school_management.payment.dto.PaymentRequestDto;
import com.example.school_management.payment.dto.PaymentResponseDto;
import com.example.school_management.student.Student;
import com.example.school_management.common.enums.ModeOfPayment;
import com.example.school_management.common.enums.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PaymentMapper {

    public Payment toEntity(PaymentRequestDto dto, Student student) {
        Payment payment = new Payment();
        payment.setTotalAmount(dto.getTotalAmount());
        payment.setPaymentDate(LocalDate.now());
        payment.setModeofpayment(ModeOfPayment.fromString(dto.getModeofpayment()));
        payment.setRemarks(dto.getRemarks());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(dto.getTransactionId());
        payment.setUpiId(dto.getUpiId());
        payment.setBankName(dto.getBankName());
        payment.setChequeNumber(dto.getChequeNumber());
        payment.setChequeDate(dto.getChequeDate());
        payment.setStudent(student);
        return payment;
    }

    public PaymentResponseDto toResponseDto(Payment payment, String message) {
        return PaymentResponseDto.builder()
                .receiptNumber(payment.getReceiptNumber())
                .referenceNumber(payment.getReferenceNumber())
                .paymentDate(payment.getPaymentDate())
                .status(payment.getStatus().name())
                .totalAmount(payment.getTotalAmount())
                .Modeofpayment(payment.getModeofpayment().name())
                .message(message)
                .build();
    }
}