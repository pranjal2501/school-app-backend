package com.example.school_management.attendance.service;

import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayRequestDto;
import com.example.school_management.attendance.dto.schoolHoliday.SchoolHolidayResponseDto;
import com.example.school_management.attendance.entity.SchoolHolidays;
import com.example.school_management.attendance.mapper.HolidayMapper;
import com.example.school_management.attendance.repository.SchoolHolidaysRepository;
import com.example.school_management.common.RoleVerifier;
import com.example.school_management.common.enums.Role;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolHolidayService {
    private final SchoolHolidaysRepository holidayRepo;
    public final HolidayMapper holidayMapper;
    public final RoleVerifier verify;

    public boolean isHoliday(LocalDate date) {

        return holidayRepo.existsByHolidayDate(date);
    }

    @Transactional
    public List<SchoolHolidayResponseDto> addHolidays(List<SchoolHolidayRequestDto> dtos) throws AccessDeniedException {

        if (!verify.hasRole(Role.ADMIN)) {
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }

        List<SchoolHolidays> entitiesToSave = new ArrayList<>();
        for (SchoolHolidayRequestDto dto : dtos) {
            if (dto.getHolidayDate().getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException("Date " + dto.getHolidayDate() + " is a Sunday. Sundays are already non-working days.");
            }

            if (holidayRepo.existsByHolidayDate(dto.getHolidayDate())) {
                throw new IllegalStateException("Holiday already exists for date: " + dto.getHolidayDate());
            }

            entitiesToSave.add(holidayMapper.toEntity(dto));
        }

        List<SchoolHolidays> savedEntities = holidayRepo.saveAll(entitiesToSave);

        return savedEntities.stream()
                .map(holidayMapper::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public List<SchoolHolidayResponseDto> getHolidaysByYear(String academicYear) {
        return holidayRepo.findAllByAcademicYear(academicYear)
                .stream()
                .map(holidayMapper::toResponse)
                .toList();
    }

    public void deleteHoliday(Long id) throws AccessDeniedException {

        if(!verify.hasRole(Role.ADMIN)){
            throw new AccessDeniedException("Only ADMIN can perform this action");
        }

        SchoolHolidays holiday = holidayRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Holiday not found with ID: " + id));

        // Constraint: Don't allow deleting a holiday that has already passed
        if (holiday.getHolidayDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot delete a holiday that has already passed.");
        }

        holidayRepo.delete(holiday);
    }

    @Transactional
    public boolean isDateHoliday(LocalDate date) {
        return holidayRepo.existsByHolidayDate(date);
    }
}

