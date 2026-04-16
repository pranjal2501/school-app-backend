package com.example.school_management.attendance.entity;

import com.example.school_management.attendance.JsonListConverter;
import com.example.school_management.common.BaseEntity;
import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(
        name = "student_attendances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_class_date",
                        columnNames = {"class_id", "attendance_date"}
                )
        },
        indexes = {
                @Index(name = "idx_sa_year_date", columnList = "academicYear, attendance_date")
        }
)
@Builder
public class StudentAttendance extends BaseEntity {

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(nullable = false, length = 10)
    private String academicYear;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Integer> absentRollNumbers = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Integer> lateRollNumbers = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonListConverter.class)
    private List<Integer> halfDayRollNumbers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private SchoolClass classEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by", nullable = false)
    private Teacher markedBy;
}