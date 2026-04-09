package com.example.school_management.subject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>
{
    Optional<Subject> findBySubjectCode(Integer subjectCode);
    boolean existsBySubjectCode(Integer subjectCode);
}
