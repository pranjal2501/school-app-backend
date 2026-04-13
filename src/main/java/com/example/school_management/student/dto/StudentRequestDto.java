package com.example.school_management.student.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.*;
import com.example.school_management.parent.entity.Parent;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentRequestDto {

    @NotBlank(message = ValidationMessages.FIRST_NAME_REQUIRED)
    private String firstName;

    private String middleName;

    @NotBlank(message = ValidationMessages.LAST_NAME_REQUIRED)
    private String lastName;

    @NotNull(message = ValidationMessages.GENDER_REQUIRED)
    private Gender gender;

    @NotNull(message = ValidationMessages.DOB_REQUIRED)
    @Past(message = ValidationMessages.DOB_PAST)
    private LocalDate dob;

    @NotNull(message = ValidationMessages.BLOOD_GROUP_REQUIRED)
    private BloodGroup bloodGroup;

    @Min(value = 1, message = ValidationMessages.ROLL_NUMBER_MIN)
    private Integer rollNumber;

    @NotBlank(message = ValidationMessages.AADHAR_REQUIRED)
    @Pattern(regexp = "^[0-9]{12}$", message = ValidationMessages.AADHAR_PATTERN)
    private String aadharNo;

    @NotBlank(message = ValidationMessages.NATIONALITY_REQUIRED)
    private String nationality;

    @NotNull(message = ValidationMessages.RELIGION_REQUIRED)
    private Religion religion;

    private Standard standard;

    private Division division;

    @NotNull(message = ValidationMessages.CATEGORY_REQUIRED)
    private Category category;

    private Long parentId;

    private Parent parent;

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