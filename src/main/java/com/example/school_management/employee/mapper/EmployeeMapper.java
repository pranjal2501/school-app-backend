package com.example.school_management.employee.mapper;

import com.example.school_management.employee.dto.EmployeeResponseDto;
import com.example.school_management.employee.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponseDto toResponseDto(Employee employee) {
        return EmployeeResponseDto.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .mobile(employee.getMobile())
                .gender(employee.getGender())
                .dob(employee.getDob())
                .joiningDate(employee.getJoiningDate())
                .role(employee.getRole())
                .isActive(employee.isActive())
                .profilePicUrl(employee.getProfilePicUrl())
                .yearsOfExperienceInCurrentSchool(employee.getYearsOfExperienceInCurrentSchool())
                .build();
    }
}