package com.example.school_management.grievance.bonafide.util;

import com.example.school_management.student.entity.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BonafidePlaceholderResolver {

    public String resolve(String template, Student student) {
        return template
                .replace("{{student.name}}",
                        nullSafe(student.getFirstName()) + " " + nullSafe(student.getLastName()))
                .replace("{{student.class}}",
                        student.getAssignedClass() != null ? nullSafe(student.getAssignedClass().getClassName()) : "N/A")
                .replace("{{student.division}}",
                        student.getAssignedClass() != null && student.getAssignedClass().getAcademicStructure() != null
                                ? nullSafe(student.getAssignedClass().getAcademicStructure().getDivision()) : "N/A")
                .replace("{{student.rollNumber}}",
                        student.getRollNumber() != null ? String.valueOf(student.getRollNumber()) : "N/A")
                .replace("{{student.dob}}",
                        student.getDob() != null ? student.getDob().toString() : "N/A")
                .replace("{{student.category}}",
                        student.getCategory() != null ? student.getCategory().name() : "N/A")
                .replace("{{student.academicYear}}",
                        resolveAcademicYear())
                .replace("{{student.religion}}",
                        student.getReligion() != null ? student.getReligion().name() : "N/A")
                .replace("{{student.nationality}}",
                        nullSafe(student.getNationality()));
    }

    private String nullSafe(String value) {
        return value != null ? value : "N/A";
    }

    private String resolveAcademicYear() {
        int year = LocalDate.now().getYear();
        return year + "-" + (year + 1);
    }
}