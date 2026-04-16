package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayRequestDto;
import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayResponseDto;
import com.example.school_management.attendance.entity.SchoolHolidays;
import org.springframework.stereotype.Component;

@Component
public class HolidayMapper {

    public SchoolHolidays toEntity(SchoolHolidayRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return SchoolHolidays.builder()
                .holidayDate(dto.getHolidayDate())
                .description(dto.getDescription())
                .academicYear(dto.getAcademicYear())
                .build();
    }

    public SchoolHolidayResponseDto toResponse(SchoolHolidays entity) {
        if (entity == null) {
            return null;
        }

        // Safely extract the day of the week before building the DTO
        String dayOfWeek = (entity.getHolidayDate() != null)
                ? entity.getHolidayDate().getDayOfWeek().name()
                : null;

        return SchoolHolidayResponseDto.builder()
                .id(entity.getId())
                .academicYear(entity.getAcademicYear())
                .holidayDate(entity.getHolidayDate())
                .description(entity.getDescription())
                .dayOfWeek(dayOfWeek)
                .build();
    }

    public void updateEntity(SchoolHolidayRequestDto dto, SchoolHolidays entity) {
        if (dto == null || entity == null) {
            return;
        }

        // We continue to use setters here to mutate the existing, attached Hibernate entity
        if (dto.getHolidayDate() != null) {
            entity.setHolidayDate(dto.getHolidayDate());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getAcademicYear() != null) {
            entity.setAcademicYear(dto.getAcademicYear());
        }
    }
}