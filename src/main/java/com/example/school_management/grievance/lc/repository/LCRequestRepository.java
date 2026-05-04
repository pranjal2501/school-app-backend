package com.example.school_management.grievance.lc.repository;

import com.example.school_management.common.enums.GrievanceStatus;
import com.example.school_management.grievance.lc.entity.LCRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LCRequestRepository extends JpaRepository<LCRequest, Long> {

    List<LCRequest> findByRequestedById(Long parentId);
    List<LCRequest> findByStatus(GrievanceStatus status);
    boolean existsByStudentIdAndStatus(Long studentId, GrievanceStatus status);
}