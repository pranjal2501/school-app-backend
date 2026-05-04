package com.example.school_management.grievance.lc.controller;

import com.example.school_management.grievance.lc.dto.*;
import com.example.school_management.grievance.lc.service.LCRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grievance/lc")
@RequiredArgsConstructor
public class LCRequestController {

    private final LCRequestService service;

    // ── PARENT ─────────────────────────────────────────────────────────────────

    @PostMapping("/submit/{parentId}")
    public ResponseEntity<LCRequestResponse> submit(
            @PathVariable Long parentId,
            @Valid @RequestBody LCCreateRequest request) throws AccessDeniedException {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.submitRequest(parentId, request));
    }

    @GetMapping("/my/{parentId}")
    public ResponseEntity<List<LCRequestResponse>> getMyRequests(
            @PathVariable Long parentId) throws AccessDeniedException {
        return ResponseEntity.ok(service.getMyRequests(parentId));
    }

    // ── ADMIN ──────────────────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<LCRequestResponse>> getAll() throws AccessDeniedException {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LCRequestResponse>> getPending() throws AccessDeniedException {
        return ResponseEntity.ok(service.getAllPendingRequests());
    }

    @PutMapping("/approve/{requestId}")
    public ResponseEntity<LCRequestResponse> approve(
            @PathVariable Long requestId,
            @Valid @RequestBody LCRequestApprove dto) throws AccessDeniedException {
        return ResponseEntity.ok(service.approveRequest(requestId, dto));
    }

    @PutMapping("/reject/{requestId}")
    public ResponseEntity<LCRequestResponse> reject(
            @PathVariable Long requestId,
            @Valid @RequestBody LCRequestReject dto) throws AccessDeniedException {
        return ResponseEntity.ok(service.rejectRequest(requestId, dto));
    }
}