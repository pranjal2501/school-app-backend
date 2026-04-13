package com.example.school_management.student;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import com.example.school_management.student.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    // 📌 Admit a single student by Admin (auto-links logged-in admin)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> admitStudent(
            @Valid @RequestBody StudentFromAdminRequestDto request,
            Principal principal) {

        StudentResponseDto response = studentService.admitStudent(request, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Student admitted successfully", response));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> admitStudentsBulk(
            @Valid @RequestBody List<StudentFromAdminRequestDto> requests,
            Principal principal) {

        List<StudentResponseDto> response = studentService.admitStudentsBulk(requests, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bulk students admitted successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> getAllStudents() {

        List<StudentResponseDto> students = studentService.getAllStudents();

        return ResponseEntity.ok(
                ApiResponse.ok("Students fetched successfully", students)
        );
    }

    // 📌 Fetch a single student by ID (Admin & Teacher access)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> getStudentById(
            @PathVariable Long id) {

        StudentResponseDto student = studentService.getStudentById(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Student fetched successfully", student)
        );
    }

    // 📌 Fully update student details (Admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto request) {

        StudentResponseDto updated = studentService.updateStudent(id, request);

        return ResponseEntity.ok(
                ApiResponse.ok("Student updated successfully", updated)
        );
    }

    // 📌 Partially update student info after admission (Admin or Parent)
    //Update the student from parents login
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudentAfterAdmit(
            @PathVariable Long id,
            @Valid @RequestBody StudentRemainingInfoDto request) {

        StudentResponseDto updated = studentService.updateStudentAfterAdmit(id, request);

        return ResponseEntity.ok(
                ApiResponse.ok("Student updated successfully", updated)
        );
    }

    // 📌 Assign the class and division with list of students id
    @PatchMapping("/assign-class")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> assignClassAndDivisionList(@Valid @RequestBody StudentClassAssignmentRequestDto request)
    {
        List<StudentResponseDto> student= studentService.assignClassAndDivision(request);

        return ResponseEntity.ok(ApiResponse.ok("Class and Division assigned successfully",student));
    }

    // 📌 Delete a student record (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Student deleted successfully", null)
        );
    }

    // 📌 Fetch profile for logged-in student/parent
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('STUDENT','PARENT')")
    public ResponseEntity<ApiResponse<String>> profile() {

        return ResponseEntity.ok(
                ApiResponse.ok("Student profile fetched", "Student Profile")
        );
    }

    // 📌 Fetch dashboard data for student/parent
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('STUDENT','PARENT')")
    public ResponseEntity<ApiResponse<String>> studentDashboard() {

        return ResponseEntity.ok(
                ApiResponse.ok("Dashboard fetched", "Student & Parent Dashboard")
        );
    }

    // 📌 Search students by standard, division, and name query (Admin & Teacher)
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentDropdownDto>>> searchStudents(
            @RequestParam Standard standard,
            @RequestParam Division division,
            @RequestParam String query) {
        List<StudentDropdownDto> response = studentService.searchStudents(standard, division, query);

        return ResponseEntity.ok(ApiResponse.ok("Students fetched successfully", response));
    }
}