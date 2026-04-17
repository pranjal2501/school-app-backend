package com.example.school_management.assignment.mapper;

import com.example.school_management.academics.dto.response.AssignmentResponseDto;
import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.assignment.dto.response.TeacherSubjectDropdownDto;
import com.example.school_management.assignment.entity.Assignment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AssignmentMapper {

    public Assignment toEntity(AssignmentRequestDto dto, ClassSubjectTeacher allocation) {
        if (dto == null || allocation == null) return null;

        return Assignment.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .documentUrl(dto.getDocumentUrl())
                .assignmentType(dto.getAssignmentType())
                .dueDate(dto.getDueDate())
                .totalMarks(dto.getTotalMarks())
                .allocation(allocation)
                .build();
    }

    public AssignmentResponseDto toResponseDto(Assignment entity) {
        if (entity == null) return null;

        ClassSubjectTeacher alloc = entity.getAllocation();
        String teacherName = alloc.getTeacher().getFirstName() + " " + alloc.getTeacher().getLastName();

        return AssignmentResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .documentUrl(entity.getDocumentUrl())
                .assignmentType(entity.getAssignmentType())
                .dueDate(entity.getDueDate())
                .totalMarks(entity.getTotalMarks())
                .createdAt(entity.getCreatedAt())
                .classId(alloc.getSchoolClass().getId())
                .className(alloc.getSchoolClass().getClassName())
                .subjectId(alloc.getSubject().getId())
                .subjectName(alloc.getSubject().getSubjectName())
                .teacherName(teacherName)
                .build();
    }
    // Add this to AssignmentMapper.java
    public List<TeacherClassDropdownDto> toClassDropdownList(List<ClassSubjectTeacher> allocations) {
        if (allocations == null || allocations.isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Group the allocations by the SchoolClass entity
        java.util.Map<com.example.school_management.schoolClass.entity.SchoolClass, List<ClassSubjectTeacher>> groupedAllocations =
                allocations.stream().collect(java.util.stream.Collectors.groupingBy(ClassSubjectTeacher::getSchoolClass));

        // 2. Map the grouped map into the nested DTO structure
        return groupedAllocations.entrySet().stream()
                .map(entry -> {
                    com.example.school_management.schoolClass.entity.SchoolClass schoolClass = entry.getKey();

                    // Build the inner list of subjects
                    List<TeacherSubjectDropdownDto> subjects = entry.getValue().stream()
                            .map(alloc -> TeacherSubjectDropdownDto.builder()
                                    .subjectId(alloc.getSubject().getId())
                                    .subjectName(alloc.getSubject().getSubjectName())
                                    .build())
                            .toList();

                    // Build the outer class wrapper
                    return TeacherClassDropdownDto.builder()
                            .classId(schoolClass.getId())
                            .className(schoolClass.getClassName())
                            .subjects(subjects)
                            .build();
                })
                .toList();
    }
}