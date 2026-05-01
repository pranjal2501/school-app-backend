package com.example.school_management.admin.entity;

import com.example.school_management.attendance.entity.LeaveRequests;
//import com.example.school_management.attendance.entity.TeacherAttendance;
import com.example.school_management.complaint.entity.Complaint;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.student.entity.Student;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100)
    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

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

//    @OneToMany(mappedBy = "markedBy")
//    private List<TeacherAttendance> teacherAttendances;

    @OneToMany(mappedBy = "approvedBy")
    private List<LeaveRequests> leaveRequests;

    @OneToMany(mappedBy = "admin", fetch = FetchType.LAZY)
    private List<UserNotification> notifications = new ArrayList<>();
}