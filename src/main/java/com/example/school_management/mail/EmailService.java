package com.example.school_management.mail;

public interface EmailService {
    void sendTeacherOnboardingEmail(String toEmail, String teacherName, String rawPassword);
}
