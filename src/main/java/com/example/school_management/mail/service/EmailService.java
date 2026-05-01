package com.example.school_management.mail.service;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.payload.StudentEmailData;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface EmailService {
    void sendTeacherOnboardingEmail(String toEmail, String teacherName, String rawPassword);
    void sendAdmissionEmail(String toEmail,
                            String studentName,
                            String parentEmail,
                            String rawPassword);

    @Async
    void sendBulkAdmissionEmails(List<StudentEmailData> emailDataList);

    void sendBonafideApproved(String toEmail, String parentName, String studentName, byte[] imageBytes);
    void sendBonafideRejected(String toEmail, String parentName, String studentName, String remarks);
    void sendForgotPasswordEmail(String toEmail, String otp);

    void sendResultEmail(String toEmail, String studentName, byte[] pdfBytes);

    void sendPasswordEmail(@Email(message = ValidationMessages.EMAIL_INVALID) @Size(max = 100) String email, String password);

    void sendParentApprovalRequestEmail(@Email(message = ValidationMessages.EMAIL_INVALID) @Size(max = 100) String email, @NotBlank(message = ValidationMessages.FATHER_FIRST_NAME_REQUIRED) @Size(max = 50) String fatherFirstName, String s, String s1, @NotNull String standard, String regId);
}
