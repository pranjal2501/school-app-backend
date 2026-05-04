package com.example.school_management.grievance.bonafide.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.grievance.bonafide.dto.*;
import com.example.school_management.grievance.bonafide.service.BonafideRequestService;
import com.example.school_management.parent.repository.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grievance/bonafide")
@RequiredArgsConstructor
public class BonafideController {

    private final BonafideRequestService requestService;
    private final ParentRepository parentRepository;

    // ── ADMIN: Request Management ──────────────────────────────────────────────

    @GetMapping("/admin/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<BonafideRequestResponse>>> getAllRequests(
            @RequestParam(required = false, defaultValue = "false") boolean pendingOnly) throws AccessDeniedException {

        List<BonafideRequestResponse> result = pendingOnly
                ? requestService.getAllPendingRequests()
                : requestService.getAllRequests();

        return ResponseEntity.ok(ApiResponse.ok("Requests fetched successfully",result));
    }

    @PostMapping("/admin/requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BonafideRequestResponse>> approveRequest(
            @PathVariable Long requestId) throws AccessDeniedException {

        return ResponseEntity.ok(ApiResponse.ok("Request approved",requestService.approveRequest(requestId)));
    }

    @PostMapping("/admin/requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BonafideRequestResponse>> rejectRequest(
            @PathVariable Long requestId,
            @Valid @RequestBody BonafideRejectRequest dto) throws AccessDeniedException {

        return ResponseEntity.ok(ApiResponse.ok("Request for bonafied rejected ",requestService.rejectRequest(requestId, dto)));
    }

    // ── PARENT: Request Flow ───────────────────────────────────────────────────

    @PostMapping("/request")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<BonafideRequestResponse>> submitRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BonafideRequestCreate dto) throws AccessDeniedException {

        Long parentId = extractParentId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Request Submitted succefully ", requestService.submitRequest(parentId, dto)));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<BonafideRequestResponse>>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) throws AccessDeniedException {

        Long parentId = extractParentId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok("Request fetched successfully",requestService.getMyRequests(parentId)));
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

