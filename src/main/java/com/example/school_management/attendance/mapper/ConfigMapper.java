package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.config.AttendanceConfigRequestDto;
import com.example.school_management.attendance.dto.config.AttendanceConfigResponseDto;
import com.example.school_management.attendance.entity.AttendanceConfig;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

    public AttendanceConfig toEntity(AttendanceConfigRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return AttendanceConfig.builder()
                .academicYear(dto.getAcademicYear())
                .studentWorkingDays(dto.getStudentWorkingDays())
                .studentMinAttendancePct(dto.getStudentMinAttendancePct())
                .build();
    }

    public AttendanceConfigResponseDto toResponseDto(AttendanceConfig entity) {
        if (entity == null) {
            return null;
        }

        return AttendanceConfigResponseDto.builder()
                .id(entity.getId())
                .academicYear(entity.getAcademicYear())
                .studentWorkingDays(entity.getStudentWorkingDays())
                .studentMinAttendancePct(entity.getStudentMinAttendancePct())
                .build();
    }

    public void updateEntity(AttendanceConfigRequestDto dto, AttendanceConfig entity) {
        if (dto != null && entity != null) {
            mapDtoToEntity(dto, entity);
        }
    }

    // Setters remain here because you cannot use a Builder to update an existing, attached Hibernate entity.
    public void mapDtoToEntity(AttendanceConfigRequestDto dto, AttendanceConfig entity) {
        entity.setAcademicYear(dto.getAcademicYear());
        entity.setStudentWorkingDays(dto.getStudentWorkingDays());
        entity.setStudentMinAttendancePct(dto.getStudentMinAttendancePct());
    }
}