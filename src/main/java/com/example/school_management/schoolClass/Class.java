package com.example.school_management.schoolClass;
//Only main class info, details of class like students and rollnum of each class is in Classroom.java

import com.example.school_management.common.ValidationMessages;
import com.example.school_management.student.Student;
import com.example.school_management.teacher.Teacher;
import com.example.school_management.common.enums.Division;
import com.example.school_management.common.enums.Standard;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Class {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = ValidationMessages.STANDARD_REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(name = "standard", nullable = false)
    private Standard standard;

    @NotNull(message = ValidationMessages.DIVISION_REQUIRED)
    @Enumerated(EnumType.STRING)
    @Column(name = "division", nullable = false)
    private Division division;

    @Min(0)
    @Column(name = "total_students")
    private Integer totalStudents;

    @Min(0)
    @Column(name = "boys")
    private Integer boys;

    @Min(0)
    @Column(name = "girls")
    private Integer girls;

    @Column(name = "class_name")
    private String className;

    @OneToOne
    @JoinColumn(name = "class_teacher_id", referencedColumnName = "id", unique = true)
    private Teacher classTeacher;

    @Min(0)
    private Integer currentBoys;

    @Min(0)
    private Integer currentGirls;

    @OneToMany(mappedBy = "assignedClass", fetch = FetchType.LAZY)
    private List<Student> students = new ArrayList<>();
}