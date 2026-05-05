package com.example.school_management.subject.repository;

import com.example.school_management.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long>
{
    Optional<Subject> findBySubjectCode(String subjectCode);
    boolean existsBySubjectCode(String subjectCode);
    List<Subject> findAllByStandard(String standard);

    // 1. Fetch Top-Level Subjects for the UI Tree (Includes Standalone & Aggregates)
    List<Subject> findAllByStandardAndParentSubjectIsNull(String standard);

    // 2. Fetch ONLY Leaf Nodes for Teacher Allocations & Exams (Hides "Social Science")
    @Query("SELECT s FROM Subject s WHERE s.standard = :standard AND s.isAggregateOnly = false")
    List<Subject> findAllLeafSubjectsByStandard(String standard);

    // 3. Fetch Children of a specific aggregate
    List<Subject> findAllByParentSubjectId(Long parentId);
}
