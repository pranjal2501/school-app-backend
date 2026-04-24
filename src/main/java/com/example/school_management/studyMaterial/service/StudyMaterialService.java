package com.example.school_management.studyMaterial.service;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import com.example.school_management.common.exception.ResourceNotFoundException;
import com.example.school_management.common.exception.UnauthorizedAccessException;
import com.example.school_management.studyMaterial.dto.ParentSubjectMaterialResponseDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadRequestDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadResponseDto;
import com.example.school_management.studyMaterial.entity.StudyMaterial;
import com.example.school_management.studyMaterial.repository.StudyMaterialRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final RoleVerifier roleVerifier;
    private final ClassSubjectTeacherRepository classSubjectTeacherRepository;

    @Transactional
    public StudyMaterialUploadResponseDto uploadStudyMaterial(StudyMaterialUploadRequestDto request) {
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository.findAllById(request.getClassSubjectTeacherIds());

        if (allocations.size() != request.getClassSubjectTeacherIds().size()) {
            throw new ResourceNotFoundException("ClassSubjectTeacher", "ids", request.getClassSubjectTeacherIds());
        }

        // Verify ownership for ALL selected classes
        if (!isAdmin) {
            for (ClassSubjectTeacher cst : allocations) {
                if (!cst.getTeacher().isIdentifiedBy(currentUser)) {
                    throw new UnauthorizedAccessException("You are not authorized to upload material for one or more selected classes.");
                }
            }
        }

        StudyMaterial studyMaterial = StudyMaterial.builder()
                .classTeachers(new HashSet<>(allocations))
                .description(request.getDescription())
                .isVisibleToParent(request.getIsVisibleToParent())
                .urls(request.getUrls() != null ? request.getUrls() : List.of())
                .build();

        return mapToResponseDto(studyMaterialRepository.save(studyMaterial));
    }

    @Transactional
    public StudyMaterialUploadResponseDto updateStudyMaterial(Long studyMaterialId, StudyMaterialUploadRequestDto request) {
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyMaterial", "id", studyMaterialId));

        // Verify ownership of existing material
        if (!isAdmin) {
            boolean ownsMaterial = studyMaterial.getClassTeachers().stream()
                    .anyMatch(cst -> cst.getTeacher().isIdentifiedBy(currentUser));
            if (!ownsMaterial) {
                throw new UnauthorizedAccessException("You can only update your own study materials.");
            }
        }

        // Fetch and verify new allocations (allowing adding/removing classes)
        List<ClassSubjectTeacher> newAllocations = classSubjectTeacherRepository.findAllById(request.getClassSubjectTeacherIds());
        if (newAllocations.size() != request.getClassSubjectTeacherIds().size()) {
            throw new ResourceNotFoundException("ClassSubjectTeacher", "ids", request.getClassSubjectTeacherIds());
        }

        if (!isAdmin) {
            for (ClassSubjectTeacher cst : newAllocations) {
                if (!cst.getTeacher().isIdentifiedBy(currentUser)) {
                    throw new UnauthorizedAccessException("You can only assign material to your own classes.");
                }
            }
        }

        studyMaterial.setClassTeachers(new HashSet<>(newAllocations));
        studyMaterial.setDescription(request.getDescription());
        studyMaterial.setIsVisibleToParent(request.getIsVisibleToParent());
        studyMaterial.setUrls(request.getUrls() != null ? request.getUrls() : List.of());

        return mapToResponseDto(studyMaterialRepository.save(studyMaterial));
    }

    public List<StudyMaterialUploadResponseDto> getStudyMaterialByClassAndSubject(Long classId, Long subjectId) {
        return studyMaterialRepository.findByClassIdAndSubjectId(classId, subjectId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<StudyMaterialUploadResponseDto> getStudyMaterialByAllocation(Long classSubjectTeacherId) {
        return studyMaterialRepository.findByClassTeachers_Id(classSubjectTeacherId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteStudyMaterial(Long studyMaterialId) {
        String currentUser = roleVerifier.getCurrentUsername();
        boolean isAdmin = roleVerifier.hasRole(Role.ADMIN);

        StudyMaterial studyMaterial = studyMaterialRepository.findById(studyMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException("StudyMaterial", "id", studyMaterialId));

        if (!isAdmin) {
            boolean ownsMaterial = studyMaterial.getClassTeachers().stream()
                    .anyMatch(cst -> cst.getTeacher().isIdentifiedBy(currentUser));
            if (!ownsMaterial) {
                throw new UnauthorizedAccessException("You can only delete your own study materials.");
            }
        }

        studyMaterialRepository.delete(studyMaterial);
    }

    @Transactional
    public List<ParentSubjectMaterialResponseDto> getSubjectWiseMaterialsForClass(Long classId) {

        // 1. Fetch all subjects taught in this class
        List<ClassSubjectTeacher> allocations = classSubjectTeacherRepository.findBySchoolClassId(classId);

        // 2. Map everything into the organized grouping
        return allocations.stream().map(cst -> {

            // Format Teacher Degree (e.g., "BACHELORS (B.Ed)")
            String degreeDisplay = cst.getTeacher().getDegreeType() != null
                    ? cst.getTeacher().getDegreeType().name()
                    : "Not Specified";

            if (cst.getTeacher().getCustomDegreeName() != null && !cst.getTeacher().getCustomDegreeName().isEmpty()) {
                degreeDisplay += " (" + cst.getTeacher().getCustomDegreeName() + ")";
            }

            // Map Teacher Info
            ParentSubjectMaterialResponseDto.TeacherInfoDto teacherInfo =
                    ParentSubjectMaterialResponseDto.TeacherInfoDto.builder()
                            .name(cst.getTeacher().getFirstName() + " " + cst.getTeacher().getLastName())
                            .degree(degreeDisplay)
                            .email(cst.getTeacher().getEmail())
                            .mobile(cst.getTeacher().getMobile())
                            .build();

            // Map & Filter Study Materials (Only visible to parents)
            List<ParentSubjectMaterialResponseDto.ParentStudyMaterialDto> materialsList =
                    cst.getStudyMaterials().stream()
                            .filter(StudyMaterial::getIsVisibleToParent)
                            .map(sm -> ParentSubjectMaterialResponseDto.ParentStudyMaterialDto.builder()
                                    .id(sm.getId())
                                    .description(sm.getDescription())
                                    .urls(sm.getUrls())
                                    .build())
                            .collect(Collectors.toList());

            // Build the final Subject Group
            return ParentSubjectMaterialResponseDto.builder()
                    .subjectId(cst.getSubject().getId())
                    .subjectName(cst.getSubject().getSubjectName())
                    .subjectCode(cst.getSubject().getSubjectCode())
                    .subjectType(cst.getSubject().getSubjectType() != null ? cst.getSubject().getSubjectType().name() : "N/A")
                    .totalMarks(cst.getSubject().getTotalMarks())
                    .teacher(teacherInfo)
                    .materials(materialsList)
                    .build();

        }).collect(Collectors.toList());
    }

    // Manual mapping ensures we extract the Lists of class names perfectly
    private StudyMaterialUploadResponseDto mapToResponseDto(StudyMaterial sm) {
        List<String> classNames = sm.getClassTeachers().stream()
                .map(cst -> cst.getSchoolClass().getClassName())
                .collect(Collectors.toList());

        Set<Long> allocationIds = sm.getClassTeachers().stream()
                .map(ClassSubjectTeacher::getId)
                .collect(Collectors.toSet());

        // Assuming all allocations are for the same subject and teacher
        ClassSubjectTeacher firstAllocation = sm.getClassTeachers().iterator().next();

        return StudyMaterialUploadResponseDto.builder()
                .studyMaterialId(sm.getId())
                .classSubjectTeacherIds(allocationIds)
                .schoolClasses(classNames)
                .subject(firstAllocation.getSubject().getSubjectName())
                .teacher(firstAllocation.getTeacher().getFirstName() + " " + firstAllocation.getTeacher().getLastName())
                .description(sm.getDescription())
                .isVisibleToParent(sm.getIsVisibleToParent())
                .urls(sm.getUrls())
                .build();
    }
}