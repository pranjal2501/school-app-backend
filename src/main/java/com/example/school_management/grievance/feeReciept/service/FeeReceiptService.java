package com.example.school_management.grievance.feeReciept.service;

import com.example.school_management.common.enums.LetterheadType;
import com.example.school_management.fees.entity.FeeAllocation;
import com.example.school_management.fees.entity.FeeInstallment;
import com.example.school_management.grievance.template.dto.PlaceholderContext;
import com.example.school_management.grievance.template.entity.LetterheadTemplate;
import com.example.school_management.grievance.template.service.LetterheadTemplateService;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeeReceiptService {

    private final LetterheadTemplateService templateService;
    private final FeeReceiptImageService imageService;
    private final EmailService emailService;

    public void sendFeeReceiptEmail(FeeInstallment installment) {
        try {
            FeeAllocation allocation = installment.getFeeAllocation();
            Student student = allocation.getStudent();
            Parent parent = student.getParent();

            if (parent == null || parent.getEmail() == null) {
                log.warn("No parent email for student: {}", student.getRegId());
                return;
            }

            LetterheadTemplate template = templateService
                    .getByTypeEntity(LetterheadType.FEE_RECEIPT);

            PlaceholderContext context = PlaceholderContext.builder()
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .studentRegId(student.getRegId())
                    .studentStandard(student.getStandard())
                    .studentAcademicYear(allocation.getAcademicYear())
                    .feeInstallmentNumber(String.valueOf(installment.getInstallmentNumber()))
                    .feeTotalInstallments(String.valueOf(allocation.getInstallments().size()))
                    .feeAmountPaid(String.valueOf(installment.getAmount()))
                    .feePaymentDate(String.valueOf(installment.getPaidAt().toLocalDate()))
                    //.remarks(installment.getRemarks() != null ? installment.getRemarks() : "-")
                    .feeTotalFee(String.valueOf(allocation.getTotalAmount()))
                    .build();

            byte[] imageBytes = imageService.generate(template, context);

            String studentName = student.getFirstName() + " " + student.getLastName();
            String parentName = parent.getFatherFirstName() + " " + parent.getFatherLastName();

            emailService.sendFeeReceiptEmail(
                    parent.getEmail(),
                    parentName,
                    studentName,
                    imageBytes
            );

        } catch (Exception e) {
            log.error("Failed to send fee receipt for installment: {}. Error: {}",
                    installment.getId(), e.getMessage());
        }
    }
}