package com.example.school_management.student.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.BloodGroup;
import com.example.school_management.common.enums.Category;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.Religion;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentRemainingInfoDto {

    @NotNull(message = ValidationMessages.GENDER_REQUIRED)
    private Gender gender;

    private BloodGroup bloodGroup;

    @NotBlank(message = ValidationMessages.NATIONALITY_REQUIRED)
    private String nationality;

    @NotNull(message = ValidationMessages.RELIGION_REQUIRED)
    private Religion religion;

    @NotNull(message = ValidationMessages.CATEGORY_REQUIRED)
    private Category category;

    private String previousSchool;

    @DecimalMin(value = "0.0", message = ValidationMessages.PERCENTAGE_MIN)
    @DecimalMax(value = "100.0", message = ValidationMessages.PERCENTAGE_MAX)
    private Double previousPercentage;

    private String birthCertificateUrl;
    private String aadharPhotoUrl;
    private String previousMarksheetUrl;
    private String passportPhotoUrl;
    private String leavingCertificateUrl;
    private String casteCertificateUrl;
    private String incomeCertificateUrl;
}
