package com.example.school_management.attendance.mapper;

import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayRequestDto;
import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayResponseDto;
import com.example.school_management.attendance.entity.AttendanceConfig;
import com.example.school_management.attendance.entity.SchoolHolidays;
import com.example.school_management.attendance.repository.AttendanceConfigRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HolidayMapper {

    private final AttendanceConfigRepo attendanceConfigRepo;

    public SchoolHolidays toEntity(SchoolHolidayRequestDto dto) {
        if (dto == null) {
            return null;
        }

        AttendanceConfig config = attendanceConfigRepo.findTopByOrderByCreatedAtDesc();
        String academicYear = config.getAcademicYear();

        return SchoolHolidays.builder()
                .holidayDate(dto.getHolidayDate())
                .description(dto.getDescription())
                .academicYear(academicYear)
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

        if (dto.getHolidayDate() != null) {
            entity.setHolidayDate(dto.getHolidayDate());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}