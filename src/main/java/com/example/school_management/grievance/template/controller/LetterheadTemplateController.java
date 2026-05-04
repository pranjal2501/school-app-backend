package com.example.school_management.grievance.template.controller;

import com.example.school_management.common.enums.LetterheadType;
import com.example.school_management.common.enums.PlaceholderKey;
import com.example.school_management.grievance.template.dto.LetterheadTemplateRequestDto;
import com.example.school_management.grievance.template.dto.LetterheadTemplateResponseDto;
import com.example.school_management.grievance.template.service.LetterheadTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/grievance/templates")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class LetterheadTemplateController {

    private final LetterheadTemplateService service;

    @PostMapping
    public ResponseEntity<LetterheadTemplateResponseDto> create(
            @Valid @RequestBody LetterheadTemplateRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<LetterheadTemplateResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LetterheadTemplateResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<LetterheadTemplateResponseDto> getByType(@PathVariable LetterheadType type) {
        return ResponseEntity.ok(service.getByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LetterheadTemplateResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody LetterheadTemplateRequestDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/placeholders")
    public ResponseEntity<List<Map<String, String>>> getPlaceholders() {
        List<Map<String, String>> placeholders = Arrays.stream(PlaceholderKey.values())
                .map(key -> Map.of(
                        "token", key.getToken(),
                        "displayName", key.getDisplayName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(placeholders);
    }
}