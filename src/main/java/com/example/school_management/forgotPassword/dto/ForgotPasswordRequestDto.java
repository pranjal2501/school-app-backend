package com.example.school_management.forgotPassword.dto;

import com.example.school_management.common.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ForgotPasswordRequestDto {

    @NotNull
    private Role role;

    @NotNull
    private String email;
}
