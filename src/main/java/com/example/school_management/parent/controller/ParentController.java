package com.example.school_management.parent.controller;

import com.example.school_management.common.ApiResponse;
import com.example.school_management.parent.dto.ChildDetailsResponseDto;
import com.example.school_management.parent.dto.ChildSummaryResponseDto;
import com.example.school_management.parent.dto.ParentsDashboardResponseDto;
import com.example.school_management.parent.service.ParentService;
import com.example.school_management.student.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('PARENT')")
public class ParentController {

    private final ParentService parentService;
//    Dashboard
//    GET /api/v1/parents/dashboard/{studentId} — attendance %, avg score, pending assignments, fees due, recent activity
//    @GetMapping("/dashboard/{student_id}")
//    public ResponseEntity<ApiResponse<String>> parentDashboard(@PathVariable Long id) {
//
//        StudentResponseDto student=parentService.getStudentProfileById(id);
//
//        return ResponseEntity.ok(
//                ApiResponse.ok("Dashboard fetched", "Admin Dashboard")
//        );
//    }
//
//    Results
//    GET /api/v1/parents/children/{studentId}/results?examType=MID_TERM — results per exam type with subject wise marks, grade, avg, highest, lowest
//


    @GetMapping("/child-list")
    public ResponseEntity<ApiResponse<List<ChildSummaryResponseDto>>> getChildList()
    {
        return ResponseEntity.ok(ApiResponse.ok("List fetched successfully", parentService.getChildList()));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<ParentsDashboardResponseDto>> getDashboard(
            @RequestParam String registrationId) {
        return ResponseEntity.ok(ApiResponse.ok("Dashboard fetched Successfully. ", parentService.getDashboard(registrationId)));
    }


    @GetMapping("/child-details/{RegId}")
    public ResponseEntity<ApiResponse<ChildDetailsResponseDto>> getChildDetails(@PathVariable String RegId){
        return ResponseEntity.ok(ApiResponse.ok("Student fetched successfully", parentService.getChildDetails(RegId)));
    }

}
