package com.example.school_management.timeTable.repository;

import com.example.school_management.timeTable.entity.TimeTableConfig;
import com.example.school_management.timeTable.entity.TimetableSlot;
import com.example.school_management.timeTable.service.TimetableService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TimetableConfigRepository extends JpaRepository<TimeTableConfig, Long> {
    Optional<TimeTableConfig> findByAcademicYear(String academicYear);
}
