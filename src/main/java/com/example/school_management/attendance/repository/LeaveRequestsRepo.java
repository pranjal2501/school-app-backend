package com.example.school_management.attendance.repository;

import com.example.school_management.attendance.entity.LeaveRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveRequestsRepo extends JpaRepository<LeaveRequests, Long> {
}
