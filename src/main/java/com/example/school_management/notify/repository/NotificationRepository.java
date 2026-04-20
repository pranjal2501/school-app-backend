package com.example.school_management.notify.repository;

import com.example.school_management.notify.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n " +
            "LEFT JOIN FETCH n.targetClasses " +
            "LEFT JOIN FETCH n.teacherIds " +
            "WHERE n.dispatched = false " +
            "AND n.scheduledDispatchTime < :now")
    List<Notification> findPendingNotifications(@Param("now") LocalDateTime now);
}
