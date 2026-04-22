package com.example.school_management.complaint.repository;

import com.example.school_management.complaint.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    Optional<Complaint> findByTicketNumber(String ticketNumber);
    List<Complaint> findByReportedByEmployee_EmailOrderByCreatedAtDesc(String email);
    List<Complaint> findByTargetStudent_AssignedClass_IdOrderByCreatedAtDesc(Long classId);
}