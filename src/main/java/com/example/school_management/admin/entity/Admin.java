package com.example.school_management.admin.entity;

import com.example.school_management.attendance.entity.LeaveRequests;
import com.example.school_management.attendance.entity.TeacherAttendance;
import com.example.school_management.complaint.entity.Complaint;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(max = 50)
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @OneToMany(mappedBy = "reportedByAdmin", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Complaint> complaints = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "addedBy", fetch = FetchType.LAZY)
    List<Student> admittedStudents = new ArrayList<>();

    @OneToMany(mappedBy = "markedBy")
    private List<TeacherAttendance> teacherAttendances;

    @OneToMany(mappedBy = "approvedBy")
    private List<LeaveRequests> leaveRequests;
}