package com.example.school_management.specialEventFees.repository;

import java.util.List;

import com.example.school_management.notify.entity.Notification;
import com.example.school_management.specialEventFees.entity.SpecialEventFee;
import com.example.school_management.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Optional;

public interface SpecialEventFeeRepository extends JpaRepository<SpecialEventFee, Long> {
    List<SpecialEventFee> findByCreatedByTeacher(Teacher teacher);

    SpecialEventFee findByEventName(String title);

    Optional<SpecialEventFee> findByNotification(Notification n);
}
