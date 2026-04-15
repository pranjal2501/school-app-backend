package com.example.school_management.attendance.entity;

import com.example.school_management.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "school_holidays",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_holiday_date", columnNames = {"holidayDate"}
        )
)
@Data
@NoArgsConstructor // Required by Hibernate
@AllArgsConstructor // For Builder/Easy instantiation
public class SchoolHolidays extends BaseEntity {

    @Column(nullable = false)
    private LocalDate holidayDate;

    @Column(nullable = false, length = 100)
    private String description;

    @Column(nullable = false, length = 10)
    private String academicYear;
}