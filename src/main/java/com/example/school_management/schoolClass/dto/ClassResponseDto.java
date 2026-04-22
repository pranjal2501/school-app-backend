package com.example.school_management.schoolClass.dto;

import lombok.Data;

@Data
public class ClassResponseDto {
    private Long id;
    private String standard;
    private String division;
    private Integer totalStudents;
    private Integer boys;
    private Integer girls;
    private String className;
    private Integer currentBoys;
    private Integer currentGirls;
    private Integer vacantSeatsBoys;
    private Integer vacantSeatsGirls;
    private Integer totalVacantSeats;

    // Extracted safely from the Teacher entity
    private Long classTeacherId;
    private String classTeacherName;
}