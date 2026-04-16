package com.example.school_management.admin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private boolean isActive;

}