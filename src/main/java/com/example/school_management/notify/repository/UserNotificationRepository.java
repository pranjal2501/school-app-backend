package com.example.school_management.notify.repository;


import com.example.school_management.notify.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
    // get all notifications for a parent
    List<UserNotification> findByParentId(Long parentId);

    // get all notifications for a teacher
    List<UserNotification> findByTeacherId(Long teacherId);

    // get unread notifications for a parent
    List<UserNotification> findByParentIdAndIsReadFalse(Long parentId);

    // get unread notifications for a teacher
    List<UserNotification> findByTeacherIdAndIsReadFalse(Long teacherId);
}