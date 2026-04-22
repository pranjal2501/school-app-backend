package com.example.school_management.assignment.mapper;

import com.example.school_management.assignment.dto.request.StudentSubmissionDto;
import com.example.school_management.assignment.dto.response.SubmissionResponseDto;
import com.example.school_management.assignment.entity.Assignment;
import com.example.school_management.assignment.entity.AssignmentSubmission;
import com.example.school_management.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class SubmissionMapper {

    public AssignmentSubmission toEntity(StudentSubmissionDto dto, Assignment assignment, Student student) {
        return AssignmentSubmission.builder()
                .assignment(assignment)
                .student(student)
                .isSubmitted(dto.getIsSubmitted())
                .build();
    }

    public SubmissionResponseDto toResponseDto(AssignmentSubmission submission) {
        Student student = submission.getStudent();
        return SubmissionResponseDto.builder()
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(student.getRollNumber())
                .isSubmitted(submission.isSubmitted())
                .build();
    }
}