package com.example.school_management.teacher.dto.response;

import com.example.school_management.common.enums.DegreeType;
import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.enums.Specialization;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TeacherResponseDto {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    // Basic Info
    private String teacherId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private Gender gender;
    private LocalDate dob;
    private LocalDate joiningDate;
    private String aadharNo;
    private String panNo;

    // Address
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;

    // Education & URLs
    private DegreeType degreeType;
    private String customDegreeName;
    private String profilePicUrl;
    private String aadharPhotourl;
    private String panPhotourl;
    private String degreeCertificateurl;
    private String resignationLetterurl;
    private String resumeurl;

    // Experience
    private Integer yearsOfExperienceInCurrentSchool;
    private String previousSchool;
    private Integer yearsOfExperienceInPreviousSchool;

    // Collections (Updated for Many-to-Many)
    private List<String> subjects;
    private List<Specialization> specializations;

    // System Status
    private Role role;
    private boolean active;
}