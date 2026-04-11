package com.example.school_management.mail;

import com.example.school_management.common.payload.StudentEmailData;
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
}
