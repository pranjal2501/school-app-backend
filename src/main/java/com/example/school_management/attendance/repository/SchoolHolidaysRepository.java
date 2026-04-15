package com.example.school_management.attendance.repository;

import com.example.school_management.attendance.entity.SchoolHolidays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SchoolHolidaysRepository extends JpaRepository<SchoolHolidays, Long> {
    boolean existsByHolidayDate(LocalDate attendanceDate);

    List<SchoolHolidays> findByHolidayDateBetween(LocalDate from, LocalDate to);

    Optional<SchoolHolidays> findByHolidayDate(LocalDate date);

    List<SchoolHolidays> findAllByAcademicYear(String academicYear);
}
