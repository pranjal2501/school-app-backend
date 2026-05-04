package com.example.school_management.specialEventFees.repository;

import java.util.List;
import java.util.Optional;

import com.example.school_management.parent.entity.Parent;
import com.example.school_management.specialEventFees.entity.SpecialEventFee;
import com.example.school_management.specialEventFees.entity.SpecialEventFeePayment;
import com.example.school_management.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpecialEventFeePaymentRepository extends JpaRepository<SpecialEventFeePayment, Long> {
    int countBySpecialEventFeeAndFeePaid(SpecialEventFee fee, boolean b);

    List<SpecialEventFeePayment> findBySpecialEventFee(SpecialEventFee fee);

    SpecialEventFeePayment findByParent(Parent parent);

    Optional<SpecialEventFeePayment> findBySpecialEventFeeAndParent(SpecialEventFee specialEvent, Parent parent);

    List<SpecialEventFeePayment> findByStudent(Student stud);
}
