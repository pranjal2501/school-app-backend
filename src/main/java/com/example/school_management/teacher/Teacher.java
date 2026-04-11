package com.example.school_management.teacher;

import com.example.school_management.attendance.entity.LeaveRequests;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.attendance.entity.TeacherAttendance;
import com.example.school_management.common.enums.Specialization;
import com.example.school_management.complaint.Complaint;
import com.example.school_management.employee.Employee;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.schoolClass.Class;
import com.example.school_management.subject.Subject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Teacher extends Employee {

    @Size(max = 100)
    @Column(name = "previous_school", length = 100)
    private String previousSchool;

    @PositiveOrZero
    @Max(50)
    @Column(name = "years_of_experience")
    private Integer yearsOfExperienceInPreviousSchool;

    @OneToOne(mappedBy = "classTeacher")
    private Class assignedClass;

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
    private List<TeacherAttendance> teacherAttendances;

    @OneToMany(mappedBy = "teacher")
    private List<LeaveRequests> leaveRequests;
}