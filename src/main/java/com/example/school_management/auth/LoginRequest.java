package com.example.school_management.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email or mobile no. is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}