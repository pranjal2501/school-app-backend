package com.example.school_management.academics;

import com.example.school_management.academics.dto.ClassOverrideRequestDto;
import com.example.school_management.academics.dto.ClassSubjectAllocationRequestDto;
import com.example.school_management.academics.dto.StandardTemplateRequestDto;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/academic-setup")
@RequiredArgsConstructor
// Enables Spring's method-level validation for @PathVariable and @RequestParam
@Validated
public class AcademicSetupController {

    private final AcademicSetupService academicSetupService;

    @PostMapping("/standards/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> applyStandardTemplate(
            @Valid @RequestBody StandardTemplateRequestDto request,
            // Extracts the authenticated admin's identity directly from the security context
            @AuthenticationPrincipal UserDetails userDetails) {

        // Passing the admin username down to the service to record exactly who applied the template
        academicSetupService.applyTemplateToStandard(request, userDetails.getUsername());

        // Standardized the response to use your custom ApiResponse wrapper
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Template successfully applied to all classes in " + request.getStandard(), null));
    }

    @PatchMapping("/classes/{classId}/override")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> overrideSubjectTeacher(
            @PathVariable Long classId,
            @Valid @RequestBody ClassOverrideRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        academicSetupService.overrideSubjectTeacher(classId, request, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok("Teacher override applied successfully for class.", null));
    }

    @DeleteMapping("/classes/{classId}/subjects/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeSubjectFromClass(
            @PathVariable Long classId,
            @PathVariable Long subjectId,
            @AuthenticationPrincipal UserDetails userDetails) {

        academicSetupService.removeSubjectFromClass(classId, subjectId, userDetails.getUsername());

        return ResponseEntity.ok(ApiResponse.ok("Subject successfully removed from the class.", null));
    }

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