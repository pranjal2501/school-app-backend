package com.example.school_management.entity;

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

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "teacher_subjects",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )

    private List<Subject> subjects = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialization_subject_id")
    private Subject specialization;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Complaint> complaints = new ArrayList<>();
}