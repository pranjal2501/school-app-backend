package com.example.school_management.academics;

import com.example.school_management.academics.AcademicSetupService;
import com.example.school_management.academics.dto.ClassOverrideRequestDto;
import com.example.school_management.academics.dto.ClassSubjectAllocationRequestDto;
import com.example.school_management.academics.dto.StandardTemplateRequestDto;
import com.example.school_management.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/academic-setup")
@RequiredArgsConstructor
public class AcademicSetupController {

    private final AcademicSetupService academicSetupService;

    // Handles Point #2 and #3
    @PostMapping("/standards/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> applyStandardTemplate(@Valid @RequestBody StandardTemplateRequestDto request) {
        academicSetupService.applyTemplateToStandard(request);
        return ResponseEntity.ok("Template successfully applied to all classes in " + request.getStandard());
    }

    // Handles Point #4
    @PatchMapping("/classes/{classId}/override")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> overrideSubjectTeacher(
            @PathVariable Long classId,
            @Valid @RequestBody ClassOverrideRequestDto request) {

        academicSetupService.overrideSubjectTeacher(classId, request);
        return ResponseEntity.ok("Teacher override applied successfully for class.");
    }

    /**
     * Removes a subject allocation from a specific class (e.g., Deleting Sanskrit from 10-L)
     */
    @DeleteMapping("/classes/{classId}/subjects/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeSubjectFromClass(
            @PathVariable Long classId,
            @PathVariable Long subjectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        academicSetupService.removeSubjectFromClass(classId, subjectId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Subject successfully removed from the class.", null));
    }

    /**
     * Creates a brand new subject and instantly assigns it to the class (e.g., Creating Hindi just for 10-L)
     */
    @PostMapping("/classes/{classId}/subjects/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createAndAllocateSubject(
            @PathVariable Long classId,
            @Valid @RequestBody StandardTemplateRequestDto.SubjectTemplateDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        academicSetupService.createAndAllocateSubjectToClass(classId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("New subject created and allocated successfully.", null));
    }

    /**
     * Allocates an already existing subject to a class
     */
    @PostMapping("/classes/{classId}/subjects/allocate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> allocateExistingSubject(
            @PathVariable Long classId,
            @Valid @RequestBody ClassSubjectAllocationRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        academicSetupService.allocateExistingSubjectToClass(classId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Existing subject allocated to class successfully.", null));
    }
}