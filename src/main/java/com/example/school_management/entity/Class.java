package com.example.school_management.entity;

import com.example.school_management.constants.ValidationMessages;
import com.example.school_management.enums.Division;
import com.example.school_management.enums.Standard;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
}