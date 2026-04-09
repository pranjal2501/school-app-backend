package com.example.school_management.service;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private static final String FROM_EMAIL = ""; //enter Your email here
    private static final String APP_PASSWORD = ""; //enter your app password here

    public void sendReceiptEmail(String toEmail, String studentName,
                                 String receiptNumber, byte[] pdfBytes) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Payment Receipt - " + receiptNumber);

            MimeMultipart multipart = new MimeMultipart();


            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                    "Dear " + studentName + ",\n\n" +
                            "Please find attached your payment receipt.\n\n" +
                            "Receipt Number : " + receiptNumber + "\n\n" +
                            "Thank you for your payment!\n\n" +
                            "Regards,\n" +
                            "ABC School"
            );

            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
            attachmentPart.setDataHandler(new DataHandler(dataSource));
            attachmentPart.setFileName(receiptNumber + ".pdf");

            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }

    public static void sendAdmissionEmail(String toEmail,
                                          String studentName,
                                          String parentEmail,
                                          String rawPassword) {

        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Student Admission Successful");

            String body =
                    "Dear Parent,\n\n" +
                            "Your child has been successfully admitted.\n\n" +

                            "Student Name: " + studentName + "\n\n" +

                            "Login Credentials:\n" +
                            "Username: " + parentEmail + "\n" +
                            "Password: " + rawPassword + "\n\n" +

                            "Please change your password after login.\n\n" +
                            "Regards,\nABC School";

            message.setText(body);

            Transport.send(message);

            System.out.println("Admission email sent to: " + toEmail);

        } catch (Exception e) {
            System.out.println("Email sending failed: " + e.getMessage());
        }
    }
}