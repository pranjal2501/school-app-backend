package com.example.school_management.student.dto;

import com.example.school_management.common.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnassignedStudentResponseDto {
    private Long id;
    private String regId;
    private String firstName;
    private String lastName;
    private String standard;
    private Gender gender;
}