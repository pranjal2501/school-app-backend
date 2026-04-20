package com.example.school_management.seeder;

import com.example.school_management.academics.service.AcademicSetupService;
import com.example.school_management.admin.service.AdminService;
import com.example.school_management.assignment.service.AssignmentService;
import com.example.school_management.complaint.service.ComplaintService;
import com.example.school_management.student.service.StudentService;
import com.example.school_management.teacher.service.TeacherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseSeederService {

    private final AdminService adminService;
    private final AcademicSetupService academicSetupService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final AssignmentService assignmentService;
    private final ComplaintService complaintService;
    private final ObjectMapper objectMapper;

//    @Transactional
    public void seedDatabase() {
        try {
            log.info("Starting Database Seeding Process...");

            // Read JSON from resources folder
            InputStream inputStream = new ClassPathResource("seed-data.json").getInputStream();
            DataSeederDto data = objectMapper.readValue(inputStream, DataSeederDto.class);

            // Step 1: Create Admin (No auth needed usually, but setting it just in case)
            setSecurityContext(data.getAdmin().getUsername(), "ADMIN");
            adminService.createAdmin(data.getAdmin());
            log.info("Step 1 Complete: Admin created.");

            // Step 2 & 3: Add Standard and Division
            data.getClasses().forEach(academicSetupService::setupStandardAndClasses);
            log.info("Step 3 Complete: Standards & Divisions created.");

            // Step 4: Onboard Teachers
            data.getTeachers().forEach(t -> teacherService.onboardTeacher(t, data.getAdmin().getUsername()));
            log.info("Step 4 Complete: 10 Teachers onboarded.");

            // Step 5: Set Subjects for Standards
            data.getStandardTemplates().forEach(academicSetupService::applyTemplateToStandard);
            log.info("Step 5 Complete: Subjects allocated to standards.");

            // Step 6a: Create Students in Bulk
            // 🚀 CAPTURE THE SAVED STUDENTS TO GET THEIR REAL IDs
            var admittedStudents = studentService.admitStudentsBulk(data.getStudents());
            log.info("Step 6a Complete: 30 Students admitted.");

            // Step 6b: Assign Students to Classes
            // 🚀 DYNAMICALLY EXTRACT IDs (Distribute across A, B, C, D to prevent capacity breach)
            List<Long> divAIds = admittedStudents.subList(0, 7).stream().map(s -> s.getId()).toList();  // 7 students (~4 Boys)
            List<Long> divBIds = admittedStudents.subList(7, 14).stream().map(s -> s.getId()).toList(); // 7 students (~3 Boys)
            List<Long> divCIds = admittedStudents.subList(14, 21).stream().map(s -> s.getId()).toList(); // 7 students (~4 Boys)
            List<Long> divDIds = admittedStudents.subList(21, 30).stream().map(s -> s.getId()).toList(); // 9 students (~4 Boys)

            // 🚀 INJECT THE REAL IDs INTO THE JSON PAYLOAD BEFORE SAVING
            data.getClassAssignments().get(0).setStudentId(divAIds);
            data.getClassAssignments().get(1).setStudentId(divBIds);
            data.getClassAssignments().get(2).setStudentId(divCIds);
            data.getClassAssignments().get(3).setStudentId(divDIds);

            data.getClassAssignments().forEach(studentService::assignClassAndDivision);
            log.info("Step 6b Complete: Students safely distributed across Div A, B, C, and D.");

            // Step 7: Create Assignments (Requires switching to TEACHER context)
            for (DataSeederDto.AssignmentSeederWrapper assignmentData : data.getAssignments()) {
                setSecurityContext(assignmentData.getTeacherUsername(), "TEACHER");
                assignmentService.createAssignment(assignmentData.getPayload());
            }
            log.info("Step 7 Complete: Assignments created.");

            // Step 8: Raise Complaints (Switching back to ADMIN context)
            setSecurityContext(data.getAdmin().getUsername(), "ADMIN");

            // 🚀 DYNAMICALLY INJECT STUDENT IDs FOR COMPLAINTS
            data.getComplaints().get(0).setTargetStudentId(admittedStudents.get(0).getId());  // First student (Div A)
            data.getComplaints().get(1).setTargetStudentId(admittedStudents.get(15).getId()); // Sixteenth student (Div C)

            data.getComplaints().forEach(complaintService::raiseComplaint);
            log.info("Step 8 Complete: Complaints dynamically raised.");

            log.info("Database Seeding Completed Successfully!");

            log.info("Database Seeding Completed Successfully!");

            // Clear context for safety
            SecurityContextHolder.clearContext();

        } catch (Exception e) {
            log.error("Database seeding failed: ", e);
            throw new RuntimeException("Seeding failed: " + e.getMessage());
        }
    }

    // Helper method to trick the RoleVerifier into thinking a user is actively logged in via JWT
    private void setSecurityContext(String username, String role) {
        UserDetails userDetails = User.withUsername(username)
                .password("dummy")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + role)))
                .build();
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}