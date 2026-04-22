package com.example.school_management.studyMaterial.mapper;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadRequestDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadResponseDto;
import com.example.school_management.studyMaterial.entity.StudyMaterial;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyMaterialMapper {

    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;

    public StudyMaterial toEntity(StudyMaterialUploadRequestDto dto) {
        ClassSubjectTeacher cst = classSubjectTeacherRepository.findById(dto.getClassSubjectTeacherEntityId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassSubjectTeacher", "id", dto.getClassSubjectTeacherEntityId()));

        return StudyMaterial.builder()
                .classTeacher(cst)
                .urls(dto.getUrls())
                .build();
    }

    public StudyMaterialUploadResponseDto toResponseDto(StudyMaterial studyMaterial) {
        ClassSubjectTeacher cst = studyMaterial.getClassTeacher();

        return new StudyMaterialUploadResponseDto(
                studyMaterial.getId(),
                cst.getId(),
                cst.getSchoolClass().getClassName(),
                cst.getSubject().getSubjectName(),
                cst.getTeacher().getFirstName() + " " + cst.getTeacher().getLastName(),
                studyMaterial.getUrls()
        );
    }
}