package com.example.school_management.notify.repository;


import com.example.school_management.notify.entity.UserNotification;
import com.example.school_management.parent.entity.Parent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    boolean existsByParentIdAndNotification_TitleContainingAndDeliveredAtAfter(Long id, String s, LocalDateTime localDateTime);

    //Used in parent dasboard to show latest top 3 notifications
    List<UserNotification> findTop3ByParentOrderByDeliveredAtDesc(Parent parent);
}