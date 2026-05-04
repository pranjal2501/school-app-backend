package com.example.school_management.grievance.bonafide.repository;

import com.example.school_management.common.enums.GrievanceStatus;
import com.example.school_management.grievance.bonafide.entity.BonafideRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BonafideRequestRepository extends JpaRepository<BonafideRequest, Long> {

    List<BonafideRequest> findByStatus(GrievanceStatus status);

    List<BonafideRequest> findByRequestedById(Long parentId);

    List<BonafideRequest> findByStudentId(Long studentId);

    boolean existsByStudentIdAndStatus(Long studentId, GrievanceStatus status);
}
