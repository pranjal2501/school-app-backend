package com.example.school_management.attendance.repository;

import com.example.school_management.attendance.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAttendanceRepo extends JpaRepository<StudentAttendance, Long> {
}
