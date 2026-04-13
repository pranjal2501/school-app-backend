package com.example.school_management.schoolClass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentRollNoResponseDto {
        private Integer rollNumber;
        private String firstName;
        private String lastName;
        private String gender;
        private String regId;
}
