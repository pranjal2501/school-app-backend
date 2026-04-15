package com.example.school_management.assignment.controller;

import com.example.school_management.assignment.service.AssignmentService;
import com.example.school_management.assignment.dto.AssignmentRequestDto;
import com.example.school_management.assignment.dto.AssignmentResponseDto;
import com.example.school_management.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {
}