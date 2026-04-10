package com.example.school_management.complaint;

import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByTicketNumber(String ticketNumber);
}