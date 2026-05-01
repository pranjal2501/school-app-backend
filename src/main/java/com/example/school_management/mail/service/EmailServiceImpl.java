package com.example.school_management.mail.service;

import com.example.school_management.common.payload.StudentEmailData;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Async
    @Override
    public void sendBulkAdmissionEmails(List<StudentEmailData> emailDataList) {

        for (StudentEmailData data : emailDataList) {
            try {
                sendAdmissionEmail(
                        data.getEmail(),
                        data.getStudent().getFirstName() + " " + data.getStudent().getLastName(),
                        data.getEmail(),
                        data.getPassword()
                );
            } catch (Exception e) {
                log.error("Failed to send email to: {} | Error: {}", data.getEmail(), e.getMessage());
            }
        }
    }

    @Async
    @Override
    public void sendBonafideApproved(String toEmail, String parentName,
                                     String studentName, byte[] imageBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bonafide Certificate Approved – " + studentName);
            helper.setText(buildBonafideApprovedTemplate(parentName, studentName), true);
            helper.addAttachment(
                    "Bonafide_" + studentName.replace(" ", "_") + ".png",
                    () -> new java.io.ByteArrayInputStream(imageBytes),
                    "image/png"
            );

            mailSender.send(message);
            log.info("Bonafide approval email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send bonafide approval email to: {}. Error: {}", toEmail, e.getMessage());
        }
    }

    @Async
    @Override
    public void sendBonafideRejected(String toEmail, String parentName,
                                     String studentName, String remarks) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bonafide Request Update – " + studentName);
            helper.setText(buildBonafideRejectedTemplate(parentName, studentName, remarks), true);

            mailSender.send(message);
            log.info("Bonafide rejection email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send bonafide rejection email to: {}. Error: {}", toEmail, e.getMessage());
        }
    }

    private String buildBonafideApprovedTemplate(String parentName, String studentName) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">Bonafide Certificate Approved ✓</h2>"
                + "<p style=\"color: #333; font-size: 16px;\">Dear <strong>" + parentName + "</strong>,</p>"
                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">We are pleased to inform you that the bonafide certificate request for <strong>" + studentName + "</strong> has been <span style=\"color: #27ae60; font-weight: bold;\">approved</span> by the school administration.</p>"

                // Info box
                + "<div style=\"background-color: #f0fff4; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #27ae60;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\">📎 The bonafide certificate is attached to this email as a <strong>PDF</strong>.</p>"
                + "<p style=\"margin: 5px 0; font-size: 14px; color: #555;\">Please download and keep it safe for your records.</p>"
                + "</div>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"
                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"
                + "</div>";
    }

    private String buildBonafideRejectedTemplate(String parentName, String studentName, String remarks) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">Bonafide Request Update</h2>"
                + "<p style=\"color: #333; font-size: 16px;\">Dear <strong>" + parentName + "</strong>,</p>"
                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">We regret to inform you that the bonafide certificate request for <strong>" + studentName + "</strong> has been <span style=\"color: #e74c3c; font-weight: bold;\">rejected</span> by the school administration.</p>"

                // Reason box
                + "<div style=\"background-color: #fff5f5; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #e74c3c;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Reason:</strong></p>"
                + "<p style=\"margin: 5px 0; font-size: 16px; color: #555;\">" + remarks + "</p>"
                + "</div>"

                + "<p style=\"color: #333; font-size: 15px;\">Please visit the school administration office or submit a new request after addressing the above reason.</p>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"
                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"
                + "</div>";
    }

    @Async
    @Override
    public void sendResultEmail(String toEmail, String studentName, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject("Result Card - " + studentName);
            helper.setText(buildResultEmailTemplate(studentName), true);
            helper.addAttachment(
                    "ResultCard_" + studentName.replace(" ", "_") + ".pdf",
                    () -> new java.io.ByteArrayInputStream(pdfBytes),
                    "application/pdf"
            );

            mailSender.send(message);
            log.info("Result email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send result email to: {}. Error: {}", toEmail, e.getMessage());
        }
    }

    private String buildResultEmailTemplate(String studentName) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">Result Card 📄</h2>"
                + "<p style=\"color: #333; font-size: 16px;\">Dear Parent/Guardian,</p>"
                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">Please find attached the result card for <strong>" + studentName + "</strong>.</p>"

                + "<div style=\"background-color: #f0f4ff; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #0056b3;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\">📎 The result card is attached to this email as a <strong>PDF</strong>.</p>"
                + "<p style=\"margin: 5px 0; font-size: 14px; color: #555;\">Please download and keep it safe for your records.</p>"
                + "</div>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"
                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"
                + "</div>";
    }


    @Override
    public void sendForgotPasswordEmail(String toEmail, String otp) {
        String subject = "Password Reset OTP - School Management System";

        String htmlContent = buildForgotPasswordTemplate(toEmail, otp);

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private String buildForgotPasswordTemplate(String email, String otp) {

        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">Password Reset Request 🔐</h2>"

                + "<p style=\"color: #333; font-size: 16px;\">Dear User,</p>"

                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">We received a request to reset the password for your account associated with <strong>"
                + email + "</strong>.</p>"

                + "<p style=\"color: #333; font-size: 16px;\">Use the following One-Time Password (OTP) to proceed:</p>"

                // OTP Box
                + "<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #0056b3; text-align: center;\">"
                + "<span style=\"font-size: 28px; font-weight: bold; letter-spacing: 6px; color: #2c3e50;\">"
                + otp
                + "</span>"
                + "</div>"

                + "<p style=\"color: #555; font-size: 14px;\">This OTP is valid for a limited time. Please do not share it with anyone.</p>"

                + "<p style=\"color: #d9534f; font-size: 14px; font-weight: bold;\">If you did not request a password reset, please ignore this email or contact support immediately.</p>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"

                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"

                + "</div>";
    }

    @Override
    public void sendPasswordEmail(String email, String password) {
        String subject = "Your Account Password - School Management System";

        String htmlContent = buildPasswordEmailTemplate(email, password);

        sendHtmlEmail(email, subject, htmlContent);
    }

    private String buildPasswordEmailTemplate(String email, String password) {

        String portalUrl = "http://localhost:3000/login";

        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">Your Account Credentials 🔐</h2>"

                + "<p style=\"color: #333; font-size: 16px;\">Dear User,</p>"

                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">Your account password has been generated/updated for the account associated with <strong>"
                + email + "</strong>.</p>"

                // Credentials Box
                + "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #0056b3;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Username:</strong> " + email + "</p>"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Password:</strong> "
                + "<span style=\"background-color: #e2e8f0; padding: 3px 8px; border-radius: 4px; font-family: monospace; letter-spacing: 1px;\">"
                + password
                + "</span></p>"
                + "</div>"

                // Login Button
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + portalUrl + "\" style=\"background-color: #0056b3; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;\">Login to Portal</a>"
                + "</div>"

                + "<p style=\"color: #d9534f; font-size: 14px; font-weight: bold;\">Security Notice: Please log in and change your password immediately to keep your account secure.</p>"

                + "<p style=\"color: #555; font-size: 14px;\">If you did not request this change, please contact the school administration immediately.</p>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"

                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"

                + "</div>";
    }


    @Async
    @Override
    public void sendParentApprovalRequestEmail(
            String toEmail,
            String parentName,
            String studentName,
            String maskedAadhar,
            String standard,
            String regId) {

        String subject = "Action Required: New Child Admission Request - School Management System";
        String htmlContent = buildParentApprovalTemplate(parentName, studentName, maskedAadhar, standard, regId);
        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private String buildParentApprovalTemplate(
            String parentName,
            String studentName,
            String maskedAadhar,
            String standard,
            String regId) {

        String approveUrl = "http://localhost:8080/approve-student?regId=" + regId;
        String rejectUrl = "http://localhost:8080/reject-student?regId=" + regId;

        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">"
                + "<h2 style=\"color: #2c3e50; text-align: center;\">New Child Admission Request 👶</h2>"

                + "<p style=\"color: #333; font-size: 16px;\">Dear <strong>" + parentName + "</strong>,</p>"

                + "<p style=\"color: #333; font-size: 16px; line-height: 1.5;\">The school administration has requested to add a new child under your account. Please verify the details below and confirm if this is your child.</p>"

                // Student Details Box
                + "<div style=\"background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #0056b3;\">"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Student Name:</strong> " + studentName + "</p>"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Aadhar No:</strong> " + maskedAadhar + "</p>"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Standard:</strong> " + standard + "</p>"
                + "<p style=\"margin: 5px 0; font-size: 16px;\"><strong>Registration ID:</strong> " + regId + "</p>"
                + "</div>"

                + "<p style=\"color: #333; font-size: 16px;\">Is this your child? Please take action below:</p>"

                // Approve / Reject Buttons
                + "<div style=\"text-align: center; margin: 30px 0;\">"
                + "<a href=\"" + approveUrl + "\" style=\"background-color: #27ae60; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold; margin-right: 15px;\">✓ Yes, Approve</a>"
                + "<a href=\"" + rejectUrl + "\" style=\"background-color: #e74c3c; color: #ffffff; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;\">✗ No, Reject</a>"
                + "</div>"

                + "<p style=\"color: #d9534f; font-size: 14px; font-weight: bold;\">⚠ This request will expire in 48 hours. If you do not take action, the request will be automatically cancelled.</p>"

                + "<p style=\"color: #555; font-size: 14px;\">If you did not expect this request or believe this is an error, please contact the school administration immediately.</p>"

                + "<hr style=\"border: none; border-top: 1px solid #eee; margin: 20px 0;\" />"
                + "<p style=\"color: #555; font-size: 14px;\">Best Regards,<br><strong style=\"color: #2c3e50;\">School Administration Team</strong></p>"
                + "</div>";
    }
}