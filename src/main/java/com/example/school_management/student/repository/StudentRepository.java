package com.example.school_management.student.repository;

import com.example.school_management.schoolClass.entity.SchoolClass;
import com.example.school_management.student.dto.StudentSearchDto;
import com.example.school_management.parent.entity.Parent;
import com.example.school_management.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNumber(Integer rollNumber);
    Optional<Student> findByAadharNo(String aadharNo);
    boolean existsByAadharNo(String aadharNo);
    // @Query allows us to write strict, database-agnostic JPQL.
    // LOWER() ensures case-insensitive searching.
    @Query("SELECT s FROM Student s WHERE s.assignedClass.academicStructure.standard = :standard AND s.assignedClass.academicStructure.division = :division AND (LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR s.rollNumber = :query)")
    List<Student> searchStudentsForComplaint(
            @Param("standard") String standard,
            @Param("division") String division,
            @Param("query") String query);

    List<Student> findAllByAadharNoIn(Set<String> aadharNumbers);

    List<Student> findByAssignedClass_Id(Long classId);

    Optional<Student> findByAssignedClass_IdAndRollNumber(Long classId, Integer rollNumber);

    List<Student> findAllByAssignedClassIdAndIsActiveTrue(Long classId);

    Optional<Student> findByRegId(String RegId);

    @Query("SELECT new com.example.school_management.student.dto.StudentSearchDto(" +
            "s.id, CONCAT(s.firstName, ' ', s.lastName), s.regId, " +
            "CONCAT(c.academicStructure.standard, '-', c.academicStructure.division), s.rollNumber) " +
            "FROM Student s LEFT JOIN s.assignedClass c " +
            "WHERE s.isActive = true AND (" +
            "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(s.regId) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "CAST(s.rollNumber AS string) LIKE CONCAT('%', :query, '%'))")
    List<StudentSearchDto> searchActiveStudents(@Param("query") String query);

    List<Student> findByStandardAndAssignedClassIsNullAndIsActiveTrue(String standard);

    List<Student> findByAssignedClassIdIn(List<Long> targetClasses);

    Student findFirstByParent(Parent parent);

    int countByAssignedClassId(Long id);

    @Query("SELECT DISTINCT s.assignedClass FROM Student s " +
            "WHERE s.assignedClass IS NOT NULL " +
            "AND s.isActive = true " +
            "AND s.rollNumber IS NULL")
    List<SchoolClass> findClassesWithUnassignedRollNumbers();
}
