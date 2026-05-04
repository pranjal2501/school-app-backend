package com.example.school_management.syllabus.repository;

import com.example.school_management.syllabus.entity.SyllabusProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SyllabusProgressRepository extends JpaRepository<SyllabusProgress, Long> {

    Optional<SyllabusProgress> findByAllocationIdAndNodePath(Long allocationId, String nodePath);

    /**
     * Counts how many IMMEDIATE children of a parent path are completed.
     * If parent is "SEC_1", this counts "SEC_1/CHAP_1" and "SEC_1/CHAP_2".
     * It does NOT count "SEC_1/CHAP_1/TOPIC_1" because we only want immediate children.
     *
     * In PostgreSQL/MySQL, we use a regex to ensure there is exactly one slash after the parent path.
     */
    @Query(value = "SELECT count(sp) FROM SyllabusProgress sp " +
            "WHERE sp.allocation.id = :allocationId " +
            "AND sp.isCompleted = true " +
            "AND sp.nodePath LIKE CONCAT(:parentPath, '/%') " +
            // Ensure it's an immediate child (no extra slashes)
            "AND LENGTH(sp.nodePath) - LENGTH(REPLACE(sp.nodePath, '/', '')) = " +
            "LENGTH(:parentPath) - LENGTH(REPLACE(:parentPath, '/', '')) + 1")
    long countCompletedImmediateChildren(@Param("allocationId") Long allocationId, @Param("parentPath") String parentPath);

    List<SyllabusProgress> findAllByAllocationId(Long allocationId);
}