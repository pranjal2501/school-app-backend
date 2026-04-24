package com.example.school_management.studyMaterial.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.studyMaterial.dto.ParentSubjectMaterialResponseDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadRequestDto;
import com.example.school_management.studyMaterial.dto.StudyMaterialUploadResponseDto;
import com.example.school_management.studyMaterial.service.StudyMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/study-material")
@RequiredArgsConstructor
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;

    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudyMaterialUploadResponseDto>> uploadStudyMaterial(
            @Valid @RequestBody StudyMaterialUploadRequestDto request) {
        StudyMaterialUploadResponseDto response = studyMaterialService.uploadStudyMaterial(request);
        return ResponseEntity.ok(ApiResponse.ok("Study material uploaded successfully", response));
    }

    @PutMapping("/{studyMaterialId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<StudyMaterialUploadResponseDto>> updateStudyMaterial(
            @PathVariable Long studyMaterialId,
            @Valid @RequestBody StudyMaterialUploadRequestDto request) {
        StudyMaterialUploadResponseDto response = studyMaterialService.updateStudyMaterial(studyMaterialId, request);
        return ResponseEntity.ok(ApiResponse.ok("Study material updated successfully", response));
    }

    // Your requested endpoint: By Class and Subject
    @GetMapping("/class/{classId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'PARENT', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<StudyMaterialUploadResponseDto>>> getStudyMaterialByClassAndSubject(
            @PathVariable Long classId,
            @PathVariable Long subjectId) {
        List<StudyMaterialUploadResponseDto> response = studyMaterialService.getStudyMaterialByClassAndSubject(classId, subjectId);
        return ResponseEntity.ok(ApiResponse.ok("Study materials fetched successfully", response));
    }

    // Alternative endpoint: By exact allocation ID
    @GetMapping("/allocation/{classSubjectTeacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'PARENT', 'STUDENT')")
    public ResponseEntity<ApiResponse<List<StudyMaterialUploadResponseDto>>> getStudyMaterialByAllocation(
            @PathVariable Long classSubjectTeacherId) {
        List<StudyMaterialUploadResponseDto> response = studyMaterialService.getStudyMaterialByAllocation(classSubjectTeacherId);
        return ResponseEntity.ok(ApiResponse.ok("Study materials fetched successfully", response));
    }

    @DeleteMapping("/{studyMaterialId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<String>> deleteStudyMaterial(
            @PathVariable Long studyMaterialId) {
        studyMaterialService.deleteStudyMaterial(studyMaterialId);
        return ResponseEntity.ok(ApiResponse.ok("Study material deleted successfully", null));
    }

    @GetMapping("/class/{classId}")
    @PreAuthorize("hasAnyRole('PARENT')")
    public ResponseEntity<ApiResponse<List<ParentSubjectMaterialResponseDto>>> getStudyMaterials(
            @PathVariable Long classId) {

        List<ParentSubjectMaterialResponseDto> response = studyMaterialService.getSubjectWiseMaterialsForClass(classId);
        return ResponseEntity.ok(ApiResponse.ok("Subject-wise study materials fetched successfully", response));
    }
}