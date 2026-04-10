package com.example.school_management.teacher;

import com.example.school_management.complaint.Complaint;
import com.example.school_management.employee.Employee;
import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.schoolClass.Class;
import com.example.school_management.subject.Subject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @OneToOne(mappedBy = "classTeacher")
    private Class assignedClass;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Builder.Default
    @OneToMany(mappedBy = "subjectTeacher")
    private List<Subject> subjects = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_subject_id")
    private Subject specialization;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Complaint> complaints = new ArrayList<>();

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<UserNotification> notifications = new ArrayList<>();
}