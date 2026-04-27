package com.example.school_management.timeTable.repository;

import com.example.school_management.timeTable.entity.TimetableSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {
    List<TimetableSlot> findBySchoolClassIdAndAcademicYearOrderByDayOfWeekAscPeriodNumberAsc(Long classId, String academicYear);

    List<TimetableSlot> findByTeacherIdAndAcademicYearOrderByDayOfWeekAscPeriodNumberAsc(Long teacherId, String academicYear);
}
