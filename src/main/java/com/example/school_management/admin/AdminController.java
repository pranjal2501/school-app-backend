package com.example.school_management.admin;

import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.admin.dto.AdminResponseDto;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
//@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        return ResponseEntity.ok(
                ApiResponse.ok("Dashboard fetched", "Admin Dashboard")
        );
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<AdminResponseDto>> createAdmin(@Valid @RequestBody AdminRequestDto request) {
        AdminResponseDto response = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Admin created successfully", response));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AdminResponseDto>>> getAllAdmins() {
        List<AdminResponseDto> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(ApiResponse.ok("Admins fetched successfully", admins));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminResponseDto>> getAdminById(@PathVariable Long id) {
        AdminResponseDto admin = adminService.getAdminById(id);
        return ResponseEntity.ok(ApiResponse.ok("Admin fetched successfully", admin));
    }
}