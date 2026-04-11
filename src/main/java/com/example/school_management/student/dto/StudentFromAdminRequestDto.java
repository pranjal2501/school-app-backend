package com.example.school_management.student.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.parent.Parent;
import com.example.school_management.parent.dto.ParentCreateRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentFromAdminRequestDto {

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

    @NotBlank(message = ValidationMessages.AADHAR_REQUIRED)
    @Pattern(regexp = "^[0-9]{12}$", message = ValidationMessages.AADHAR_PATTERN)
    private String aadharNo;

    private Standard standard;

    private Long parentId;

    private ParentCreateRequestDto parent;
}
