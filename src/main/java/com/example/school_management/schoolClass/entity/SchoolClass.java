package com.example.school_management.schoolClass.entity;
//Only main class info, details of class like students and rollnum of each class is in Classroom.java

import com.example.school_management.academics.entity.AcademicStructure;
import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.student.entity.Student;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClass extends BaseEntity {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_structure_id", nullable = false, unique = true)
    private AcademicStructure academicStructure;

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

    @OneToMany(mappedBy = "classEntity")
    private List<StudentAttendance> studentAttendances;
}