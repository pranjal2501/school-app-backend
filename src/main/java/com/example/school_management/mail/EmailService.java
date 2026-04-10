package com.example.school_management.mail;


import jakarta.mail.internet.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private static JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public static void sendAdmissionEmail(String toEmail,
                                   String studentName,
                                   String parentEmail,
                                   String rawPassword) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Student Admission Successful");

        message.setText(
                "Dear Parent,\n\n" +
                        "Your child has been successfully admitted.\n\n" +

                        "Student Name: " + studentName + "\n\n" +

                        "Login Credentials:\n" +
                        "Username: " + parentEmail + "\n" +
                        "Password: " + rawPassword + "\n\n" +

                        "You can Login to url" +
                        "Regards,\nABC School"
        );

        mailSender.send(message);
    }

    public void sendReceiptEmail(String toEmail, byte[] pdfBytes) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("Payment Receipt");
        helper.setText("Please find attached receipt");

        helper.addAttachment("receipt.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(message);
    }
}