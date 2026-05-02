package com.example.school_management.employee.dto;

import com.example.school_management.common.enums.Gender;
import com.example.school_management.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private Gender gender;
    private LocalDate dob;
    private LocalDate joiningDate;
    private Role role;
    private boolean isActive;
    private String profilePicUrl;
    private Integer yearsOfExperienceInCurrentSchool;
}
