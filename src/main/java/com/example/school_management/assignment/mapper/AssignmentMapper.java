package com.example.school_management.assignment.mapper;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.assignment.dto.request.AssignmentBulkRequestDto;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.assignment.dto.response.AssignmentResponseDto;
import com.example.school_management.assignment.dto.response.TeacherClassDropdownDto;
import com.example.school_management.assignment.dto.response.TeacherSubjectDropdownDto;
import com.example.school_management.assignment.entity.Assignment;
import com.example.school_management.schoolClass.entity.SchoolClass;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssignmentMapper {

    public Assignment toEntity(AssignmentBulkRequestDto dto, ClassSubjectTeacher allocation) {
        if (dto == null || allocation == null) {
            return null;
        }

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

    public Assignment toIndividualEntity(AssignmentRequestDto dto, ClassSubjectTeacher allocation) {
        if (dto == null || allocation == null) {
            return null;
        }

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

    public com.example.school_management.assignment.dto.response.AssignmentResponseDto toResponseDto(Assignment entity) {
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
    public List<TeacherClassDropdownDto> toClassDropdownList(List<ClassSubjectTeacher> allocations) {

        // Group by SchoolClass
        Map<SchoolClass, List<ClassSubjectTeacher>> groupedByClass = allocations.stream()
                .collect(Collectors.groupingBy(ClassSubjectTeacher::getSchoolClass));

        return groupedByClass.entrySet().stream().map(entry -> {
            SchoolClass schoolClass = entry.getKey();

            // Map the subjects for this class
            List<TeacherSubjectDropdownDto> subjectDtos = entry.getValue().stream().map(alloc ->
                    TeacherSubjectDropdownDto.builder()
                            .allocationId(alloc.getId())
                            .subjectId(alloc.getSubject().getId())
                            .subjectName(alloc.getSubject().getSubjectName())
                            .build()
            ).toList();

            return TeacherClassDropdownDto.builder()
                    .classId(schoolClass.getId())
                    .className(schoolClass.getClassName())
                    .subjects(subjectDtos)
                    .build();
        }).toList();
    }
}