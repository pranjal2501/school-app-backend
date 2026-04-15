package com.example.school_management.student.repository;

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
}
