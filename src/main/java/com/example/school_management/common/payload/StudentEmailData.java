package com.example.school_management.common.payload;


import com.example.school_management.student.Student;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentEmailData {
    private Student student;
    private String email;
    private String password;
}
