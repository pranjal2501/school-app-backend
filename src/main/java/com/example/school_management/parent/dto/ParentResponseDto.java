package com.example.school_management.parent.dto;

import lombok.Data;

import java.util.List;

@Data
public class ParentResponseDto {

    private Long id;
    private String fatherFirstName;
    private String fatherMiddleName;
    private String fatherLastName;
    private String fatherOccupation;
    private String motherFirstName;
    private String motherLastName;
    private String motherOccupation;
    private String guardianFirstName;
    private String guardianMiddleName;
    private String guardianLastName;
    private String mobilePrimary;
    private String email;
    private String aadharNo;
    private List<StudentLiteDto> students;

}
