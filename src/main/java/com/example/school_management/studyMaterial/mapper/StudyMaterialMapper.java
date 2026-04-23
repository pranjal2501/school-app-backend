package com.example.school_management.studyMaterial.mapper;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadRequestDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadResponseDto;
import com.example.school_management.studyMaterial.entity.StudyMaterial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StudyMaterialMapper {

    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;

    public StudyMaterial toEntity(StudyMaterialUploadRequestDto dto) {
        List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository.findAllById(dto.getClassSubjectTeacherIds());

        if (allocations.size() != dto.getClassSubjectTeacherIds().size()) {
            throw new ResourceNotFoundException("ClassSubjectTeacher", "ids", dto.getClassSubjectTeacherIds());
        }

        return StudyMaterial.builder()
                .classTeachers(new HashSet<>(allocations))
                .urls(dto.getUrls() != null ? dto.getUrls() : new ArrayList<>())
                .isVisibleToParent(dto.getIsVisibleToParent())
                .description(dto.getDescription())
                .build();
    }

    public StudyMaterialUploadResponseDto toResponseDto(StudyMaterial studyMaterial) {
        Set<ClassSubjectTeacher> csts = studyMaterial.getClassTeachers();

        Set<Long> allocationIds = csts.stream()
                .map(ClassSubjectTeacher::getId)
                .collect(Collectors.toSet());

        List<String> classNames = csts.stream()
                .map(cst -> cst.getSchoolClass().getClassName())
                .collect(Collectors.toList());

        // Extract shared details from the first available allocation
        ClassSubjectTeacher firstAllocation = csts.iterator().next();

        return StudyMaterialUploadResponseDto.builder()
                .studyMaterialId(studyMaterial.getId())
                .classSubjectTeacherIds(allocationIds)
                .schoolClasses(classNames)
                .subject(firstAllocation.getSubject().getSubjectName())
                .teacher(firstAllocation.getTeacher().getFirstName() + " " + firstAllocation.getTeacher().getLastName())
                .description(studyMaterial.getDescription())
                .isVisibleToParent(studyMaterial.getIsVisibleToParent())
                .urls(studyMaterial.getUrls())
                .build();
    }
}