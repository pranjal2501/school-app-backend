package com.example.school_management.complaint;

import com.example.school_management.common.enums.ComplaintCategory;
import com.example.school_management.common.enums.ComplaintStatus;
import com.example.school_management.common.enums.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentId(Long studentId);

    List<Complaint> findByTeacherId(Long teacherId);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByPriority(Priority priority);

    List<Complaint> findByCategory(ComplaintCategory category);

    List<Complaint> findByStudentIdAndStatus(Long studentId, ComplaintStatus status);

    List<Complaint> findByTeacherIdAndStatus(Long teacherId, ComplaintStatus status);
}