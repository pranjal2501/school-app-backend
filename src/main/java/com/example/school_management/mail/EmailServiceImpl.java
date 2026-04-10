package com.example.school_management.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    @Async // Executes in a separate thread so the API returns instantly
    public void sendTeacherOnboardingEmail(String toEmail, String teacherName, String rawPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Welcome to School Management System - Your Login Credentials");

            // Professional HTML Email Template
            String htmlContent = buildEmailTemplate(teacherName, toEmail, rawPassword);
            helper.setText(htmlContent, true); // Set to 'true' to enable HTML

            mailSender.send(message);
            log.info("Onboarding email successfully sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send onboarding email to: {}. Error: {}", toEmail, e.getMessage());
            // We log the error but don't throw a RuntimeException so it doesn't rollback the Teacher creation
        }
    }

    private String buildEmailTemplate(String name, String username, String password) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">Welcome to Our School!</h2>"
                + "<p style=\"color: #333; font-size: 16px;\">Dear <strong>" + name + "</strong>,</p>"
                + "<p style=\"color: #333; font-size: 16px;\">Your staff profile has been successfully created by the administration. Below are your official login credentials to access the portal:</p>"
                + "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Username / Email:</strong> " + username + "</p>"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Temporary Password:</strong> <span style=\"background-color: #e2e8f0; padding: 3px 8px; border-radius: 4px; font-family: monospace;\">" + password + "</span></p>"
                + "</div>"
                + "<p style=\"color: #d9534f; font-size: 14px; font-weight: bold;\">Security Notice: Please log in and change your temporary password immediately.</p>"
                + "<p style=\"color: #333; font-size: 16px;\">Best Regards,<br><strong>School Administration Team</strong></p>"
                + "</div>";
    }
}