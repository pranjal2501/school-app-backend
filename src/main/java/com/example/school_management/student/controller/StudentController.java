package com.example.school_management.student.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.student.repository.StudentRepository;
import com.example.school_management.student.service.StudentExcelService;
import com.example.school_management.student.service.StudentService;
import com.example.school_management.student.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;
    private final StudentExcelService studentExcelService;
    private final RoleVerifier roleVerifier;
    private final StudentRepository studentRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> admitStudent(
            @Valid @RequestBody StudentFromAdminRequestDto request) {

        // 2. Grab the username cleanly
        StudentResponseDto response = studentService.admitStudent(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Student admitted successfully", response));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> admitStudentsBulk(
            @Valid @RequestBody List<StudentFromAdminRequestDto> requests) {

        List<StudentResponseDto> response = studentService.admitStudentsBulk(requests);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bulk students admitted successfully", response));
    }

    @GetMapping("/bulk/template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> downloadTemplate() throws IOException {
        byte[] template = studentExcelService.generateTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_admission_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(template);
    }


    @PostMapping("/bulk/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BulkAdmissionResponseDto>> admitFromExcel(
            @RequestParam("file") MultipartFile file) throws IOException {
        BulkAdmissionResponseDto response = studentExcelService.admitFromExcel(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bulk admission from Excel completed", response));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> getAllStudents() {
        List<StudentResponseDto> students = studentService.getAllStudents();
        return ResponseEntity.ok(ApiResponse.ok("Students fetched successfully", students));
    }

    @GetMapping("/{RegId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> getStudentByRegId(@PathVariable String RegId) {
        StudentResponseDto student = studentService.getStudentByRegId(RegId);

        return ResponseEntity.ok(ApiResponse.ok("Student fetched successfully", student));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto request) {

        StudentResponseDto updated = studentService.updateStudent(id, request);

        return ResponseEntity.ok(ApiResponse.ok("Student updated successfully", updated));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PARENT')")
    public ResponseEntity<ApiResponse<StudentResponseDto>> updateStudentAfterAdmit(
            @PathVariable Long id,
            @Valid @RequestBody StudentRemainingInfoDto request) {
        StudentResponseDto updated = studentService.updateStudentAfterAdmit(
                id, request);

        return ResponseEntity.ok(ApiResponse.ok("Student updated successfully", updated));
    }

    @PatchMapping("/assign/class")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponseDto>>> assignClassAndDivisionList(
            @Valid @RequestBody StudentClassAssignmentRequestDto request) {

        List<StudentResponseDto> student = studentService.assignClassAndDivision(request);

        return ResponseEntity.ok(ApiResponse.ok("Class and Division assigned successfully", student));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable Long id) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(ApiResponse.ok("Student deleted successfully", null));
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('STUDENT','PARENT')")
    public ResponseEntity<ApiResponse<String>> profile() {
        return ResponseEntity.ok(ApiResponse.ok("Student profile fetched for: " + roleVerifier.getCurrentUsername(), "Student Profile"));
    }

    //    @GetMapping("/search")
//    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
//    public ResponseEntity<ApiResponse<List<StudentDropdownDto>>> searchStudents(
//            @RequestParam String standard,
//            @RequestParam String division,
//            @RequestParam String query) {
//
//        List<StudentDropdownDto> response = studentService.searchStudents(standard, division, query);
//
//        return ResponseEntity.ok(ApiResponse.ok("Students fetched successfully", response));
//    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<List<StudentSearchDto>>> searchStudents(@RequestParam String query) {
        // Prevent massive payloads on empty search
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(ApiResponse.ok("Query too short", List.of()));
        }
        List<StudentSearchDto> results = studentRepository.searchActiveStudents(query.trim());
        return ResponseEntity.ok(ApiResponse.ok("Search successful", results));
    }

    @GetMapping("/unassigned/{standard}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UnassignedStudentResponseDto>>> getUnassignedStudents(
            @PathVariable String standard) {

        List<UnassignedStudentResponseDto> response = studentService.getUnassignedStudentsByStandard(standard);

        return ResponseEntity.ok(ApiResponse.ok("Unassigned students fetched successfully", response));
    }
}