package com.example.school_management.studyMaterial.controller;


import com.example.school_management.common.ApiResponse;
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

    @GetMapping("/class/{classSubjectTeacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER','PARENT')")
    public ResponseEntity<ApiResponse<List<StudyMaterialUploadResponseDto>>> getStudyMaterialByClass(
            @PathVariable Long classSubjectTeacherId) {
        List<StudyMaterialUploadResponseDto> response = studyMaterialService.getStudyMaterialByClass(classSubjectTeacherId);
        return ResponseEntity.ok(ApiResponse.ok("Study materials fetched successfully", response));
    }

    @DeleteMapping("/{studyMaterialId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<String>> deleteStudyMaterial(
            @PathVariable Long studyMaterialId) {
        studyMaterialService.deleteStudyMaterial(studyMaterialId);
        return ResponseEntity.ok(ApiResponse.ok("Study material deleted successfully", null));
    }

}
