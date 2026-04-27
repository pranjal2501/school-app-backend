package com.example.school_management.schoolClass.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.schoolClass.service.ClassService;
import com.example.school_management.schoolClass.dto.ClassRequestDto;
import com.example.school_management.schoolClass.dto.ClassResponseDto;
import com.example.school_management.schoolClass.dto.StudentRollNoResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
/**
 If you send a List<ClassRequestDto> in the @RequestBody, Spring Boot's @Valid annotation will not
 automatically validate the individual items inside the list by default. It will only check if the list
 itself exists!

 To make sure your @NotNull and @Min(0) annotations inside ClassRequestDto actually fire for every item in
 that JSON array you just made, you must add @Validated to the very top of your Controller class.
 */

public class ClassController {

    private final ClassService classService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClassResponseDto>> createClass(
            @Valid @RequestBody ClassRequestDto request) {
        ClassResponseDto response = classService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Class created successfully", response));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<ClassResponseDto>>> createClassesBulk(
            @Valid @RequestBody List<ClassRequestDto> requests) {

        List<ClassResponseDto> responses = classService.createClassesBulk(requests);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Classes successfully bulk created", responses));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassResponseDto>>> getAllClasses() {
        List<ClassResponseDto> response = classService.getAllClasses();
        return ResponseEntity.ok(ApiResponse.ok("Classes fetched successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponseDto>> getClassById(@PathVariable Long id) {
        ClassResponseDto response = classService.getClassById(id);
        return ResponseEntity.ok(ApiResponse.ok("Class fetched successfully", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClassResponseDto>> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody ClassRequestDto request) {
        ClassResponseDto response = classService.updateClass(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Class updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.ok("Class deleted successfully", null));
    }

    @PatchMapping("/{classId}/assign-teacher/{teacherId}")
    public ResponseEntity<ApiResponse<ClassResponseDto>> assignClassTeacher(
            @PathVariable Long classId,
            @PathVariable Long teacherId) {
        ClassResponseDto response = classService.assignClassTeacher(classId, teacherId);
        return ResponseEntity.ok(ApiResponse.ok("Class teacher assigned successfully", response));
    }

    @GetMapping("/not-assign-roll")
    @PreAuthorize(("hasRole('ADMIN')"))
    public ResponseEntity<ApiResponse<List<ClassResponseDto>>> findClassWhoseRollNoNotAssign()
    {
        return ResponseEntity.ok(ApiResponse.ok("Classes whose roll number not assigned yet",classService.findClassWhoseRollNoNotAssign()));
    }

    @PatchMapping("/assign-roll-number/{classId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<String>>> assignRollNumberToStudents(@PathVariable Long classId)
    {
        List<String> details=classService.assignRollNumberToStudents(classId);

        return ResponseEntity.ok(ApiResponse.ok("Roll number for class "+classId+" assigned successfully", details));
    }

    @GetMapping("/student-list/{classId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentRollNoResponseDto>>> getStudentListWithRollNum(@PathVariable Long classId)    {
        List<StudentRollNoResponseDto> response = classService.getStudentListWithRollNum(classId);
        return ResponseEntity.ok(ApiResponse.ok("Students list fetched successfully", response));
    }

    @GetMapping("/{standard}/{division}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<ClassResponseDto>> getClassByStandardAndDivision(
            @PathVariable String standard,
            @PathVariable String division) {

        ClassResponseDto response = classService.getClassByStandardAndDivision(standard, division);

        return ResponseEntity.ok(ApiResponse.ok("Class fetched successfully", response));
    }
}