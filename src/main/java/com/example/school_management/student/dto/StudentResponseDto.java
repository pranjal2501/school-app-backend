package com.example.school_management.student.dto;

import com.example.school_management.common.enums.*;
import com.example.school_management.parent.dto.ParentResponseDto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentResponseDto {

    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;

    private Integer rollNumber;
    private String aadharNo;
    private Gender gender;
    private LocalDate dob;
    private BloodGroup bloodGroup;
    private String nationality;
    private Religion religion;
    private Category category;
    private Standard standard;
    private Division division;

    private ParentResponseDto parent;

    private String previousSchool;
    private Double previousPercentage;

    private boolean isActive;
}