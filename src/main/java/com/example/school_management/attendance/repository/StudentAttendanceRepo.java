package com.example.school_management.attendance.repository;

import com.example.school_management.attendance.entity.StudentAttendance;
import com.example.school_management.schoolClass.entity.SchoolClass;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAttendanceRepo extends JpaRepository<StudentAttendance, Long> {

    //check if class attendance already marked for a date
    boolean existsByClassEntityIdAndAttendanceDate(Long classId, LocalDate date);


    // Fetch single row — used for late mark correction
    Optional<StudentAttendance> findByClassEntityIdAndAttendanceDate(
            Long classId, LocalDate date
    );

    List<StudentAttendance> findByClassEntityIdAndAttendanceDateBetween(
            Long classId, LocalDate from, LocalDate to
    );

    List<StudentAttendance> findByClassEntityIdAndAcademicYear(
            Long classId, String academicYear
    );


    // ── Check if a specific roll number is in absent list on a date ──
    // JSON_CONTAINS(column, value) returns 1 if found, 0 if not
    @Query(value = """
    SELECT * FROM student_attendances
    WHERE class_id = :classId
    AND attendance_date = :date
    AND JSON_CONTAINS(absent_roll_numbers, CAST(:rollNumber AS JSON))
    """, nativeQuery = true)
    Optional<StudentAttendance> findAbsentRecord(
            @Param("classId") Long classId,
            @Param("date") LocalDate date,
            @Param("rollNumber") Integer rollNumber
    );


    // ── Fetch all dates where a roll number appears in absent list ──
    // used for parent dashboard calendar — absent dates for one student in a month
    @Query(value = """
    SELECT * FROM student_attendances
    WHERE class_id = :classId
    AND attendance_date BETWEEN :from AND :to
    AND JSON_CONTAINS(absent_roll_numbers, CAST(:rollNumber AS JSON))
    """, nativeQuery = true)
    List<StudentAttendance> findAbsentDatesForStudent(
            @Param("classId") Long classId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("rollNumber") Integer rollNumber
    );


    // ── Count absent days for a student in academic year ──
    @Query(value = """
        SELECT COUNT(*) FROM student_attendances
        WHERE class_id = :classId
        AND academic_year = :academicYear
        AND JSON_CONTAINS(absent_roll_numbers, CAST(:rollNumber AS JSON))
        """, nativeQuery = true)
    long countAbsentDays(Long classId, String academicYear, Integer rollNumber);


    // ── Count late days for a student in academic year ──
    @Query(value = """
        SELECT COUNT(*) FROM student_attendances
        WHERE class_id = :classId
        AND academic_year = :academicYear
        AND JSON_CONTAINS(late_roll_numbers, CAST(:rollNumber AS JSON))
        """, nativeQuery = true)
    long countLateDays(Long classId, String academicYear, Integer rollNumber);


    // ── Count half day for a student in academic year ──
    @Query(value = """
        SELECT COUNT(*) FROM student_attendances
        WHERE class_id = :classId
        AND academic_year = :academicYear
        AND JSON_CONTAINS(half_day_roll_numbers, CAST(:rollNumber AS JSON))
        """, nativeQuery = true)
    long countHalfDays(Long classId, String academicYear, Integer rollNumber);

    @Query(value = "SELECT COUNT(*) FROM student_attendances WHERE class_id = :classId AND academic_year = :year", nativeQuery = true)
    long countTotalSessionsConducted(@Param("classId") Long classId, @Param("year") String year);

    List<StudentAttendance> findTop7ByClassEntityIdAndAttendanceDateLessThanEqualOrderByAttendanceDateDesc(Long classId, LocalDate date);

    Optional<StudentAttendance> findByClassEntityAndAttendanceDate(SchoolClass schoolClass, @NotNull LocalDate date);
    //List<StudentAttendance> findTopNByClassEntityIdOrderByAttendanceDateDesc(@NotNull(message = "Class ID is required") Long classId, int threshold);
    List<StudentAttendance> findByClassEntityIdOrderByAttendanceDateDesc(Long classId, Pageable pageable);
    List<StudentAttendance> findByClassEntity(SchoolClass assignedClass);
}
