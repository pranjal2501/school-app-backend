package com.example.school_management.teacher.dto;

import com.example.school_management.common.enums.DegreeType;
import com.example.school_management.common.enums.Specialization;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TeacherRemainingInfoDto {
    @Size(max = 100, message = "Address line 1 cannot exceed 100 characters")
    private String addressLine1;

    @Size(max = 100, message = "Address line 2 cannot exceed 100 characters")
    private String addressLine2;

    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;

    @Pattern(regexp = "^[0-9]{6}$", message = "Pincode must be exactly 6 digits")
    private String pincode;

    private DegreeType degreeType;

    @Size(max = 150, message = "Custom degree name cannot exceed 150 characters")
    private String customDegreeName;

    @Size(max=200)
    private String profilePicUrl;

    @Size(max = 200)
    private String aadharPhotourl;

    @Size(max = 200)
    private String panPhotourl;

    @Size(max = 200)
    private String degreeCertificateurl;

    @Size(max = 200)
    private String resignationLetterurl;

    @Size(max = 200)
    private String resumeurl;

    @Size(max = 100, message = "Previous school name cannot exceed 100 characters")
    private String previousSchool;

    @PositiveOrZero(message = "Years of experience cannot be negative")
    @Max(value = 50, message = "Years of experience cannot exceed 50")
    private Integer yearsOfExperienceInPreviousSchool;

    private List<Long> subjectIds;
    private List<Specialization> specializations;
}