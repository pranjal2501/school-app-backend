package com.example.school_management.result.mapper;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.common.enums.ExamTerm;
import com.example.school_management.result.dto.MarkResponseDto;
import com.example.school_management.result.dto.StudentMarkEntryDto;
import com.example.school_management.result.entity.StudentSubjectMark;
import com.example.school_management.student.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class MarkMapper {

    // Converts a new DTO into a brand new Entity
    public StudentSubjectMark toEntity(StudentMarkEntryDto dto, Student student, ClassSubjectTeacher allocation, ExamTerm examTerm) {
        if (dto == null || student == null || allocation == null) return null;

        return StudentSubjectMark.builder()
                .student(student)
                .allocation(allocation)
                .examTerm(examTerm)
                .theoryObtained(dto.getTheoryObtained())
                .internalObtained(dto.getInternalObtained())
                .practicalObtained(dto.getPracticalObtained())
                .projectObtained(dto.getProjectObtained())
                .oralObtained(dto.getOralObtained())
                .build();
    }

    // Mutates an existing entity (Upsert logic)
    public void updateEntity(StudentMarkEntryDto dto, StudentSubjectMark existingMark) {
        if (dto == null || existingMark == null) return;

        existingMark.setTheoryObtained(dto.getTheoryObtained());
        existingMark.setInternalObtained(dto.getInternalObtained());
        existingMark.setPracticalObtained(dto.getPracticalObtained());
        existingMark.setProjectObtained(dto.getProjectObtained());
        existingMark.setOralObtained(dto.getOralObtained());
        // Note: The @PreUpdate hook on the entity will automatically recalculate total and pass/fail!
    }

    // Converts Entity to Response DTO
    public MarkResponseDto toResponseDto(StudentSubjectMark entity) {
        if (entity == null) return null;

        Student student = entity.getStudent();

        return MarkResponseDto.builder()
                .markId(entity.getId())
                .studentId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .rollNumber(student.getRollNumber())
                .theoryObtained(entity.getTheoryObtained())
                .internalObtained(entity.getInternalObtained())
                .practicalObtained(entity.getPracticalObtained())
                .projectObtained(entity.getProjectObtained())
                .oralObtained(entity.getOralObtained())
                .totalObtained(entity.getTotalObtained())
                .isPassed(entity.isPassed())
                .build();
    }
}