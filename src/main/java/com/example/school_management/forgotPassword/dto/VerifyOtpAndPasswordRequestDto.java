package com.example.school_management.forgotPassword.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class VerifyOtpAndPasswordRequestDto {

    @NotNull
    private String email;

    @NotNull
    private String otp;
}
