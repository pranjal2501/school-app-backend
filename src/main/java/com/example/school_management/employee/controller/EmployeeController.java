package com.example.school_management.employee.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.employee.dto.EmployeeResponseDto;
import com.example.school_management.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PARENT')")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDto>>> getAllEmployees() {
        return ResponseEntity.ok(ApiResponse.ok("Employees fetched successfully",
                employeeService.getAllEmployees()));
    }
}
