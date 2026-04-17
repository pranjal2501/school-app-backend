package com.example.school_management.grievance.bonafide.controller;

import com.example.school_management.grievance.bonafide.dto.*;
import com.example.school_management.grievance.bonafide.service.BonafideRequestService;
import com.example.school_management.grievance.bonafide.service.BonafideTemplateService;
import com.example.school_management.parent.repository.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grievance/bonafide")
@RequiredArgsConstructor
public class BonafideController {

    private final BonafideRequestService requestService;
    private final BonafideTemplateService templateService;
    private final ParentRepository parentRepository;

    // ── ADMIN: Template Management ─────────────────────────────────────────────

    @PostMapping("/admin/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BonafideTemplateResponse> createTemplate(
            @Valid @RequestBody BonafideTemplateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(templateService.createTemplate(request));
    }

    @PutMapping("/admin/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BonafideTemplateResponse> updateTemplate(
            @Valid @RequestBody BonafideTemplateRequest request) {

        return ResponseEntity.ok(templateService.updateActiveTemplate(request));
    }

    @GetMapping("/admin/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BonafideTemplateResponse> getActiveTemplate() {
        return ResponseEntity.ok(templateService.getActiveTemplate());
    }

    // ── ADMIN: Request Management ──────────────────────────────────────────────

    @GetMapping("/admin/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BonafideRequestResponse>> getAllRequests(
            @RequestParam(required = false, defaultValue = "false") boolean pendingOnly) {

        List<BonafideRequestResponse> result = pendingOnly
                ? requestService.getAllPendingRequests()
                : requestService.getAllRequests();

        return ResponseEntity.ok(result);
    }

    @PostMapping("/admin/requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BonafideRequestResponse> approveRequest(
            @PathVariable Long requestId) {

        return ResponseEntity.ok(requestService.approveRequest(requestId));
    }

    @PostMapping("/admin/requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BonafideRequestResponse> rejectRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody BonafideRejectRequest dto) {

        return ResponseEntity.ok(requestService.rejectRequest(requestId, dto));
    }

    // ── PARENT: Request Flow ───────────────────────────────────────────────────

    @PostMapping("/request")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<BonafideRequestResponse> submitRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BonafideRequestCreate dto) {

        Long parentId = extractParentId(userDetails);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(requestService.submitRequest(parentId, dto));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<List<BonafideRequestResponse>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        Long parentId = extractParentId(userDetails);
        return ResponseEntity.ok(requestService.getMyRequests(parentId));
    }

    // ── private helper ─────────────────────────────────────────────────────────

    private Long extractParentId(UserDetails userDetails) {
        String identifier = userDetails.getUsername(); // this is email or mobile
        return parentRepository.findByEmail(identifier)
                .or(() -> parentRepository.findByMobilePrimary(identifier))
                .orElseThrow(() -> new EntityNotFoundException("Parent not found"))
                .getId();
    }
}

