package com.example.school_management.timeTable.entity;

import com.example.school_management.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "timetable_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeTableConfig extends BaseEntity {

    @ElementCollection
    @CollectionTable(name = "timetable_working_days",
            joinColumns = @JoinColumn(name = "time_table_config_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day")
    @Builder.Default
    private List<DayOfWeek> workingDays = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime schoolStartTime; // e.g. 09:00

    @Column(nullable = false, columnDefinition = "TIME")
    private LocalTime schoolEndTime;   // e.g. 15:00

    @Column(nullable = false)
    private Integer periodDurationMinutes; // e.g. 45

    @Column(nullable = false)
    private Integer breakAfterPeriod;  // break after which period e.g. 4

    @Column(nullable = false)
    private Integer breakDurationMinutes; // e.g. 30

    @Column(nullable = false, length = 10)
    private String academicYear;
}
