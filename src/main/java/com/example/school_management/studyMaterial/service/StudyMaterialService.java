package com.example.school_management.studyMaterial.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadRequestDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadResponseDto;
import com.example.school_management.studyMaterial.entity.StudyMaterial;
import com.example.school_management.studyMaterial.mapper.StudyMaterialMapper;
import com.example.school_management.studyMaterial.repository.StudyMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final StudyMaterialMapper studyMaterialMapper;
    private final RoleVerifier roleVerifier;
    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;

    @Transactional
    public StudyMaterialUploadResponseDto uploadStudyMaterial(StudyMaterialUploadRequestDto request) {

        String currentUser = roleVerifier.getCurrentUsername();

        ClassSubjectTeacher cst = classSubjectTeacherRepository.findById(request.getClassSubjectTeacherEntityId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassSubjectTeacher", "id", request.getClassSubjectTeacherEntityId()));

        if (!cst.getTeacher().isIdentifiedBy(currentUser)) {
            throw new UnauthorizedAccessException("You can only upload study material for your own classes.");
        }

        StudyMaterial studyMaterial = studyMaterialMapper.toEntity(request);
        return studyMaterialMapper.toResponseDto(studyMaterialRepository.save(studyMaterial));
    }

    public List<StudyMaterialUploadResponseDto> getStudyMaterialByClass(Long classSubjectTeacherId) {
        return studyMaterialRepository.findByClassTeacherId(classSubjectTeacherId)
                .stream()
                .map(studyMaterialMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public void deleteStudyMaterial(Long studyMaterialId) {

        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyMaterial", "id", studyMaterialId));

        if (!isAdmin && !studyMaterial.getClassTeacher().getTeacher().isIdentifiedBy(currentUser)) {
            throw new UnauthorizedAccessException("You can only delete your own study materials.");
        }

        studyMaterialRepository.delete(studyMaterial);
    }
}

