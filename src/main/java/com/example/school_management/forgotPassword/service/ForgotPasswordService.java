package com.example.school_management.forgotPassword.service;

import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.forgotPassword.dto.ForgotPasswordRequestDto;
import com.example.school_management.forgotPassword.dto.VerifyOtpAndPasswordRequestDto;
import com.example.school_management.forgotPassword.entity.ForgotPasswordOtp;
import com.example.school_management.forgotPassword.repository.ForgotPasswordOtpRepository;
import com.example.school_management.mail.service.EmailService;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.parent.repository.ParentRepository;
import com.example.school_management.teacher.entity.Teacher;
import com.example.school_management.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    private final ForgotPasswordOtpRepository forgotPasswordOtpRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final RoleVerifier roleVerifier;
    private final EmailService emailService;

    public String sendEmailToResetPassword(ForgotPasswordRequestDto email)
    {
        Role role= email.getRole();

        if(role.equals(Role.PARENT))
        {//parents
            Boolean status= parentRepository.findByEmail(email.getEmail()).isPresent();
            return sendOTP(status, email.getEmail());

        }
        else if (role.equals(Role.TEACHER)) {
        //Teachers
            Boolean status= teacherRepository.findByEmail(email.getEmail()).isPresent();
            return sendOTP(status, email.getEmail());
        }
        else if (role.equals(Role.ADMIN)) {
            //admin
        }
            return new String("OTP SENT TO YOUR MAIL");
    }


    public String verifyOtp(VerifyOtpAndPasswordRequestDto dto) {

        ForgotPasswordOtp entity = forgotPasswordOtpRepository
                .findTopByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No OTP found for this email"));

        if (!entity.getOtp().equals(dto.getOtp())) {
            return "Wrong OTP";
        }

        if (entity.getIsUsed()) {
            return "OTP already used";
        }

        if (entity.getExpireAt().isBefore(LocalDateTime.now())) {
            return "OTP Expired";
        }

        // valid OTP
        String password = findPassword(entity.getEmail());
        emailService.sendPasswordEmail(entity.getEmail(), password);

        entity.setIsUsed(true);
        forgotPasswordOtpRepository.save(entity);

        return "Verified successfully, check your email for your password";
    }

    //===================== HELPER METHOD =========================

    public String sendOTP(Boolean status, String email)
    {
        if (status.equals(Boolean.TRUE)) {
            Random rand = new Random();
            String otp = String.valueOf(100000 + rand.nextInt(900000));

            ForgotPasswordOtp entity = ForgotPasswordOtp.builder()
                    .email(email)
                    .otp(otp)
                    .isUsed(false)
                    .build();

            forgotPasswordOtpRepository.save(entity);
            log.info("OTP saved for: {}", email);

            emailService.sendForgotPasswordEmail(email, otp);
            log.info("OTP email sent to: {}", email);
        } else {
            log.warn("Email not found in DB: {}", email);
        }
        return "OTP SENT SUCCESSFULLY";
    }

    public String findPassword(String email)
    {
        //teacher
        if(teacherRepository.findByEmail(email).isPresent()) {

            Teacher teacher= teacherRepository.findByEmail(email).get();
            String namePart = getSafeSubstring(teacher.getLastName(), 3).toUpperCase(Locale.ROOT);
            String mobilePart = getSafeSubstring(teacher.getMobile(), 3);
            String aadharPart = getSafeSubstring(teacher.getAadharNo(), 3);
            String rawPassword = namePart + mobilePart + aadharPart;
            return rawPassword;
        }
        //parents
        else if (parentRepository.findByEmail(email).isPresent()) {

            Parent parent= parentRepository.findByEmail(email).get();
            return safe(parent.getFatherLastName()).toUpperCase() +
                    safe(parent.getMobilePrimary()) +
                    safe(parent.getAadharNo());
        }
        return null;
    }

    private String getSafeSubstring(String value, int length) {
        if (value == null) return "";
        return value.length() < length ? value : value.substring(0, length);
    }
    private String safe(String value) {
        return value.length() >= 3 ? value.substring(0, 3) : value;
    }


}
