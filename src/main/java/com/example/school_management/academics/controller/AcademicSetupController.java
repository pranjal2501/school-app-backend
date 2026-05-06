package com.example.school_management.academics.controller;

import com.example.school_management.academics.dto.request.ClassBulkSetupRequestDto;
import com.example.school_management.academics.dto.request.ClassOverrideRequestDto;
import com.example.school_management.academics.dto.request.ClassSubjectAllocationRequestDto;
import com.example.school_management.academics.dto.request.StandardTemplateRequestDto;
import com.example.school_management.academics.dto.response.AcademicStructureResponseDto;
import com.example.school_management.academics.dto.response.SubjectWithTeachersResponseDto;
import com.example.school_management.academics.service.AcademicSetupService;
import com.example.school_management.common.ApiResponse;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.subject.dto.BulkSubjectCreateRequestDto;
import com.example.school_management.subject.dto.newImpl.SubjectTreeResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic-setup")
@RequiredArgsConstructor
// Enables Spring's method-level validation for @PathVariable and @RequestParam
@Validated
@CrossOrigin("*")
public class AcademicSetupController {

    private final AcademicSetupService academicSetupService;

    @GetMapping("/standards/{standard}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubjectWithTeachersResponseDto>>> getSubjectsForStandard(
            @PathVariable String standard) {
        List<SubjectWithTeachersResponseDto> response = academicSetupService.getSubjectsByStandard(standard);
        return ResponseEntity.ok(ApiResponse.ok("Subjects fetched for standard: " + standard, response));
    }

    @PostMapping("/classes/bulk-setup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ClassResponseDto>>> setupClassesBulk(
            @Valid @RequestBody ClassBulkSetupRequestDto request) {

        List<ClassResponseDto> responses = academicSetupService.setupStandardAndClasses(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Standard and classes successfully auto-generated.", responses));
    }

    /**
     * Fetches the actual active allocations (Subject + assigned Teacher) for a specific physical class
     */
    @GetMapping("/classes/{classId}/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubjectTreeResponseDto>>> getSubjectsForClass(
            @PathVariable Long classId) {
        List<SubjectTreeResponseDto> response = academicSetupService.getAllocationsByClass(classId);

        return ResponseEntity.ok(ApiResponse.ok("Allocations fetched for class.", response));
    }

    @PostMapping("/standards/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> applyStandardTemplate(
            @Valid @RequestBody StandardTemplateRequestDto request) {

        // Passing the admin username down to the service to record exactly who applied the template
        academicSetupService.applyTemplateToStandard(request);

        // Standardized the response to use your custom ApiResponse wrapper
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Template successfully applied to all classes in " + request.getStandard(), null));
    }

    @PatchMapping("/classes/{classId}/override")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> overrideSubjectTeacher(
            @PathVariable Long classId,
            @Valid @RequestBody ClassOverrideRequestDto request) {

        academicSetupService.overrideSubjectTeacher(classId, request);

        return ResponseEntity.ok(ApiResponse.ok("Teacher override applied successfully for class.", null));
    }

    @DeleteMapping("/classes/{classId}/subjects/{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> removeSubjectFromClass(
            @PathVariable Long classId,
            @PathVariable Long subjectId) {

        academicSetupService.removeSubjectFromClass(classId, subjectId);

        return ResponseEntity.ok(ApiResponse.ok("Subject successfully removed from the class.", null));
    }

    @PostMapping("/classes/{classId}/subjects/new")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> createAndAllocateSubject(
            @PathVariable Long classId,
            @Valid @RequestBody BulkSubjectCreateRequestDto.SubjectNodeDto request) {

        academicSetupService.createAndAllocateSubjectToClass(classId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("New subject created and allocated successfully.", null));
    }

    @PostMapping("/classes/{classId}/subjects/allocate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> allocateExistingSubject(
            @PathVariable Long classId,
            @Valid @RequestBody ClassSubjectAllocationRequestDto request) {
        academicSetupService.allocateExistingSubjectToClass(classId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Existing subject allocated to class successfully.", null));
    }

    /**
     *  Fetches a list of all available distinct Standards (e.g., ["6", "7", "10"])
     */
    @GetMapping("/standards")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> getAllStandards() {
        List<String> standards = academicSetupService.getAllDistinctStandards();
        return ResponseEntity.ok(ApiResponse.ok("Distinct standards fetched successfully", standards));
    }

    /**
     *  Fetches the Divisions (and their IDs) belonging to a specific Standard
     */
    @GetMapping("/standards/{standard}/divisions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AcademicStructureResponseDto>>> getDivisionsForStandard(
            @PathVariable String standard) {

        List<AcademicStructureResponseDto> divisions = academicSetupService.getDivisionsByStandard(standard);
        return ResponseEntity.ok(ApiResponse.ok("Divisions fetched for standard: " + standard, divisions));
    }
}