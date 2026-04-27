package com.example.school_management.timeTable.entity;

import com.example.school_management.common.BaseEntity;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.subject.entity.Subject;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "timetable_slots",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"class_id", "day_of_week", "period_number", "academic_year"}),
                @UniqueConstraint(columnNames = {"teacher_id", "day_of_week", "period_number", "academic_year"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableSlot extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private Integer periodNumber;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, length = 10)
    private String academicYear;
}
