package com.example.school_management.parent.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChildDetailsResponseDto {

    private String name;

    private String schoolClass;

    private Integer rollNo;

    private String classTeacher;

    private String academicYear;

    private Float attendancePercent;

}
