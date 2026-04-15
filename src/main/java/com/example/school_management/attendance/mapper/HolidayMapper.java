package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayRequestDto;
import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayResponseDto;
import com.example.school_management.attendance.entity.SchoolHolidays;
import org.springframework.stereotype.Component;

@Component
public class HolidayMapper {

    public SchoolHolidays toEntity(SchoolHolidayRequestDto dto) {
        if (dto == null) return null;

        SchoolHolidays entity = new SchoolHolidays();
        entity.setHolidayDate(dto.getHolidayDate());
        entity.setDescription(dto.getDescription());
        entity.setAcademicYear(dto.getAcademicYear());
        return entity;
    }

    public SchoolHolidayResponseDto toResponse(SchoolHolidays entity) {
        if (entity == null) return null;

        SchoolHolidayResponseDto dto = new SchoolHolidayResponseDto();
        dto.setId(entity.getId());
        dto.setAcademicYear(entity.getAcademicYear());
        dto.setHolidayDate(entity.getHolidayDate());
        dto.setDescription(entity.getDescription());

        if (entity.getHolidayDate() != null) {
            dto.setDayOfWeek(entity.getHolidayDate().getDayOfWeek().name());
        }

        return dto;
    }

    public void updateEntity(SchoolHolidayRequestDto dto, SchoolHolidays entity) {
        if (dto == null || entity == null) return;

        if (dto.getHolidayDate() != null) entity.setHolidayDate(dto.getHolidayDate());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getAcademicYear() != null) entity.setAcademicYear(dto.getAcademicYear());
    }
}