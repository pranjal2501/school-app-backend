package com.example.school_management.syllabus.controller;

import com.example.school_management.academics.entity.ClassSubjectTeacher;
import com.example.school_management.academics.repository.ClassSubjectTeacherRepository;
import com.example.school_management.common.ApiResponse;
import com.example.school_management.student.entity.Student;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.syllabus.dto.ParentSubjectDto;
import com.example.school_management.syllabus.dto.SyllabusProgressDtos;
import com.example.school_management.syllabus.service.SyllabusProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/syllabus")
@RequiredArgsConstructor
public class SyllabusProgressController {

    private final SyllabusProgressService syllabusProgressService;
    private final ClassSubjectTeacherRepository pivotRepository;
    private final StudentRepository studentRepository;

    @PutMapping("/teachers/allocation/{allocationId}/progress")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Void>> toggleProgress(
            @PathVariable Long allocationId,
            @RequestBody SyllabusProgressDtos.ToggleRequest request) {
        syllabusProgressService.toggleProgressAsTeacher(allocationId, request.getNodePath(), request.isCompleted());
        return ResponseEntity.ok(ApiResponse.ok("Progress updated successfully", null));
    }

    @GetMapping("/teachers/allocation/{allocationId}/progress")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<SyllabusProgressDtos.ProgressResponse>> getProgressForTeacher(@PathVariable Long allocationId) {

        var progressMap = syllabusProgressService.getProgressForTeacher(allocationId);
        var response = new SyllabusProgressDtos.ProgressResponse(allocationId, progressMap);
        return ResponseEntity.ok(ApiResponse.ok("Progress fetched successfully", response));
    }

    @GetMapping("/admin/allocation/{allocationId}/progress")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SyllabusProgressDtos.ProgressResponse>> getProgressForAdmin(@PathVariable Long allocationId) {

        var progressMap = syllabusProgressService.getProgressMap(allocationId);
        var response = new SyllabusProgressDtos.ProgressResponse(allocationId, progressMap);
        return ResponseEntity.ok(ApiResponse.ok("Progress fetched successfully", response));
    }

    @GetMapping("/parent/allocation/{allocationId}/progress")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<SyllabusProgressDtos.ProgressResponse>> getProgressForParent(@PathVariable Long allocationId) {

        var progressMap = syllabusProgressService.getProgressForParent(allocationId);
        var response = new SyllabusProgressDtos.ProgressResponse(allocationId, progressMap);
        return ResponseEntity.ok(ApiResponse.ok("Progress fetched successfully", response));
    }

    @GetMapping("/parent/subjects-with-progress")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<ParentSubjectDto>>> getSubjectsForParent(@RequestParam String registrationId) {
        Student student = studentRepository.findByRegId(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with Registration ID: " + registrationId));

        Long classId = student.getAssignedClass().getId();
        List<ClassSubjectTeacher> allocations = pivotRepository.findAllBySchoolClassId(classId);

        List<ParentSubjectDto> response = allocations.stream()
                .map(a -> new ParentSubjectDto(
                        a.getSubject().getId(),
                        a.getSubject().getSubjectName(),
                        a.getId()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.ok("Subjects fetched", response));
    }
}