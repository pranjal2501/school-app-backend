package com.example.school_management.attendance.repository;

import com.example.school_management.attendance.entity.AttendanceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttendanceConfigRepo extends JpaRepository<AttendanceConfig, Long> {
    Optional<AttendanceConfig> findByAcademicYear(String academicYear);
}
