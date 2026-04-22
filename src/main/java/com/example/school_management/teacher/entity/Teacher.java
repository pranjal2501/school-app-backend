package com.example.school_management.teacher.entity;

import com.example.school_management.attendance.entity.LeaveRequests;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.common.enums.Specialization;
import com.example.school_management.employee.entity.Employee;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.subject.entity.Subject;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Teacher extends Employee {

    @Column(name = "teacher_id", unique = true, updatable = false)
    private String teacherId;

    @Size(max = 100)
    @Column(name = "previous_school", length = 100)
    private String previousSchool;

    @PositiveOrZero
    @Max(50)
    @Column(name = "years_of_experience")
    private Integer yearsOfExperienceInPreviousSchool;

    @OneToOne(mappedBy = "classTeacher")
    private SchoolClass assignedClass;

    // Many-To-Many relationship with Subjects
    @ManyToMany
    @JoinTable(
            name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @Builder.Default
    private List<Subject> subjects = new ArrayList<>();

    // Element Collection for List of Enums
    @ElementCollection(targetClass = Specialization.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "teacher_specializations",
            joinColumns = @JoinColumn(name = "teacher_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "specialization")
    @Builder.Default
    private List<Specialization> specializations = new ArrayList<>();

//    @JsonIgnore
//    @Builder.Default
//    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Complaint> complaints = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<UserNotification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "markedBy")
    private List<StudentAttendance> studentAttendances;

    @OneToMany(mappedBy = "teacher")
    private List<LeaveRequests> leaveRequests;

    @PrePersist
    protected void generateTeacherId() {
        if (this.teacherId == null || this.teacherId.trim().isEmpty()) {
            int currentYear = Year.now().getValue();

            // Extract an 8-character block from a fresh UUID for high collision resistance
            String uniqueHash = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

            // Format: TCH-2026-A1B2C3D4
            this.teacherId = String.format("TCH-%d-%s", currentYear, uniqueHash);
        }
    }
}