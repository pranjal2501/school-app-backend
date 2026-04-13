package com.example.school_management.subject;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.subject.dto.SubjectRequestDto;
import com.example.school_management.subject.dto.SubjectResponseDto;
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
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Validated // Required when validating a List<> of DTOs in the request body
public class SubjectController {
}