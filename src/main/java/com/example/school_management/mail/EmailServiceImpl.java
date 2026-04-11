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
    @Async
    public void sendTeacherOnboardingEmail(String toEmail, String teacherName, String rawPassword) {
        String title = "Welcome to Our School!";
        String contextMessage = "Your staff profile has been successfully created by the administration. Below are your official login credentials to access the teacher portal:";

        String htmlContent = buildCredentialEmailTemplate(title, teacherName, contextMessage, toEmail, rawPassword);
        sendHtmlEmail(toEmail, "Welcome to School Management System - Your Login Credentials", htmlContent);
    }

    @Override
    @Async
    public void sendAdmissionEmail(String toEmail, String studentName, String parentEmail, String rawPassword) {
        String title = "Student Admission Successful";
        String contextMessage = "Congratulations! Your child, <strong>" + studentName + "</strong>, has been successfully admitted to our school. Below are your Parent Portal login credentials to track their academic progress, attendance, and fees:";

        // Using "Parent/Guardian" as the greeting since we might not have the parent's exact first name here
        String htmlContent = buildCredentialEmailTemplate(title, "Parent/Guardian", contextMessage, parentEmail, rawPassword);
        sendHtmlEmail(toEmail, "Student Admission Successful - Parent Portal Login", htmlContent);
    }

    /**
     * Reusable helper to send HTML emails, hiding the MimeMessage boilerplate.
     */
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML

            mailSender.send(message);
            log.info("HTML email successfully sent to: {} with subject: '{}'", toEmail, subject);

        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}. Error: {}", toEmail, e.getMessage());
            // Logged but not thrown to prevent rolling back the database transaction
        }
    }

    /**
     * A standardized, beautiful HTML template generator for any email sending credentials.
     */
    private String buildCredentialEmailTemplate(String title, String recipientName, String contextMessage, String username, String password) {
        // Appending the login URL if you have a frontend URL. Change this to your actual deployed URL later.
        String portalUrl = "http://localhost:3000/login";

        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">" + title + "</h2>"
                + "<p style=\"color: #333; font-size: 16px;\">Dear <strong>" + recipientName + "</strong>,</p>"
                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">" + contextMessage + "</p>"

                // Credentials Box
                + "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #0056b3;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Username:</strong> " + username + "</p>"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Temporary Password:</strong> <span style=\"background-color: #e2e8f0; padding: 3px 8px; border-radius: 4px; font-family: monospace; letter-spacing: 1px;\">" + password + "</span></p>"
                + "</div>"

                // Login Button/Link
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + portalUrl + "\" style=\"background-color: #0056b3; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;\">Login to Portal</a>"
                + "</div>"

                + "<p style=\"color: #d9534f; font-size: 14px; font-weight: bold;\">Security Notice: Please log in and change your temporary password immediately for your account's safety.</p>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"
                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"
                + "</div>";
    }
}