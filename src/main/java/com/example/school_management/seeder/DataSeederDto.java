package com.example.school_management.seeder;

import com.example.school_management.academics.dto.request.ClassBulkSetupRequestDto;
import com.example.school_management.academics.dto.request.StandardTemplateRequestDto;
import com.example.school_management.admin.dto.AdminRequestDto;
import com.example.school_management.assignment.dto.request.AssignmentRequestDto;
import com.example.school_management.complaint.dto.ComplaintRequestDto;
import com.example.school_management.student.dto.StudentClassAssignmentRequestDto;
import com.example.school_management.student.dto.StudentFromAdminRequestDto;
import com.example.school_management.teacher.dto.TeacherFromAdminRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class DataSeederDto {
    private AdminRequestDto admin;
    private List<ClassBulkSetupRequestDto> classes;
    private List<TeacherFromAdminRequestDto> teachers;
    private List<StandardTemplateRequestDto> standardTemplates;
    private List<StudentFromAdminRequestDto> students;
    private List<StudentClassAssignmentRequestDto> classAssignments;
    private List<AssignmentSeederWrapper> assignments;
    private List<ComplaintRequestDto> complaints;

    @Data
    public static class AssignmentSeederWrapper {
        private String teacherUsername; // Needed to mock the SecurityContext
        private AssignmentRequestDto payload;
    }
}