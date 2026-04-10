package com.example.school_management.parent.dto;

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.student.Student;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
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
    private List<Student> students = new ArrayList<>();

}
