package com.example.school_management.parent.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.AadharOwnerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ParentCreateRequestDto {

    @NotBlank(message = ValidationMessages.FATHER_FIRST_NAME_REQUIRED)
    private String fatherFirstName;

    @NotBlank(message = "Father middle name is required")
    private String fatherMiddleName;

    @NotBlank(message = ValidationMessages.FATHER_LAST_NAME_REQUIRED)
    private String fatherLastName;

    private String fatherOccupation;

    @NotBlank(message = ValidationMessages.MOTHER_FIRST_NAME_REQUIRED)
    private String motherFirstName;

    @NotBlank(message = "Mother middle name is required")
    private String motherMiddleName;

    @NotBlank(message = ValidationMessages.MOTHER_LAST_NAME_REQUIRED)
    private String motherLastName;

    private String motherOccupation;

    @NotBlank(message = ValidationMessages.MOBILE_PRIMARY_REQUIRED)
    @Pattern(regexp = "^[0-9]{10}$", message = ValidationMessages.MOBILE_PRIMARY_PATTERN)
    private String mobilePrimary;

    @NotBlank(message = ValidationMessages.EMAIL_REQUIRED)
    @Email(message = ValidationMessages.EMAIL_INVALID)
    private String email;

    private String password;

    @NotBlank(message = ValidationMessages.AADHAR_REQUIRED)
    @Pattern(regexp = "^[0-9]{12}$", message = ValidationMessages.AADHAR_PATTERN)
    private String aadharNo;

    private AadharOwnerType aadharOwnerType;
}
