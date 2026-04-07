package com.example.school_management.repo;

import com.example.school_management.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRollNumber(Integer rollNumber);
    boolean existsByAadharNoIn(List<String> aadharNos);

    Optional<Student> findByAadharNo(String aadharNo);

    List<Student> findByAadharNoIn(List<String> aadharNos);

    boolean existsByAadharNo(String aadharNo);

    boolean existsByRollNumber(Integer rollNumber);

    List<Student> findAllByIsActiveTrue();

    List<Student> findAllByIsActiveFalse();
}
